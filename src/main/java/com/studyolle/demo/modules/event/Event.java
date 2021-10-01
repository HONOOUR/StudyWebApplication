package com.studyolle.demo.modules.event;

import com.studyolle.demo.modules.study.Study;
import com.studyolle.demo.modules.account.Account;
import com.studyolle.demo.modules.account.UserAccount;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(
        name = "Event.withEnrollments",
        attributeNodes = @NamedAttributeNode("enrollments")
)

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = true)
    private int limitOfEnrollments;

    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public boolean isEnrollableFor(UserAccount userAccount) {
        return isNotClosed() && !isAttended(userAccount) && !isAlreadyEnrolled(userAccount);
    }

    public boolean isDisenrollableFor(UserAccount userAccount) {
        return isNotClosed() && !isAttended(userAccount) && isAlreadyEnrolled(userAccount);
    }

    public boolean isAttended(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment e: this.enrollments) {
            if (e.getAccount().equals(account) && e.isAttended()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAlreadyEnrolled(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment e: this.enrollments) {
            if (e.getAccount().equals(account) && e.isAttended()) {
                return true;
            }
        }
        return false;
    }

    public boolean isNotClosed() {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

    public int numberOfRemainSpots() {
        return this.limitOfEnrollments - (int)this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    public int getNumberOfAcceptedEnrollments() {
        int acceptedEnrollmentsCount = 0;
        for (Enrollment e: this.enrollments) {
            if (e.isAccepted()) {
                acceptedEnrollmentsCount++;
            }
        }
        return acceptedEnrollmentsCount;
    }

    public boolean canAccept(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && !enrollment.isAccepted();
    }

    public boolean canReject(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && enrollment.isAccepted();
    }

    public boolean isAbleToAcceptWaitingEnrollment() {
        if (this.eventType == EventType.FCFS && this.getNumberOfAcceptedEnrollments()< this.limitOfEnrollments) {
            return true;
        }
        return false;
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setEvent(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
        enrollment.setEvent(null);
    }

    private Enrollment getTheFirstWaitingEnrollment() {
        for (Enrollment e: this.enrollments) {
            if (!e.isAccepted()) {
                return e;
            }
        }
        return null;
    }

    public void acceptNextWaitingEnrollment() {
        if (this.isAbleToAcceptWaitingEnrollment()) {
            Enrollment enrollmentToAccept = this.getTheFirstWaitingEnrollment();
            if (enrollmentToAccept != null) {
                enrollmentToAccept.setAccepted(true);
            }
        }
    }

    public void acceptWaitingList() {
        if (this.isAbleToAcceptWaitingEnrollment()) {
            var waitingList = getWaitingList();
            int numberToAccept = this.limitOfEnrollments - this.getNumberOfAcceptedEnrollments();
            for (var e : waitingList) {
                e.setAccepted(true);
                numberToAccept--;
                if (numberToAccept == 0) {
                    break;
                }
            }
        }
    }

    private List<Enrollment> getWaitingList() {
        List<Enrollment> enrollmentsToAccept =  new ArrayList<Enrollment>();
        for (Enrollment e: this.enrollments) {
            if (!e.isAccepted()) {
                enrollmentsToAccept.add(e);
            }
        }
        return enrollmentsToAccept;
    }

    public void accept(Enrollment enrollment) {
          if (this.eventType == EventType.CONFIRMATIVE && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments()) {
              enrollment.setAccepted(true);
          }
    }

    public void reject(Enrollment enrollment) {
        if (this.eventType == EventType.CONFIRMATIVE) {
            enrollment.setAccepted(false);
        }
    }
}
