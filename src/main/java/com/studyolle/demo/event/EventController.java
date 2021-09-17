package com.studyolle.demo.event;

import com.studyolle.demo.domain.Account;
import com.studyolle.demo.domain.Event;
import com.studyolle.demo.domain.Study;
import com.studyolle.demo.event.form.EventForm;
import com.studyolle.demo.event.validator.EventValidator;
import com.studyolle.demo.modules.account.CurrentAccount;
import com.studyolle.demo.modules.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

    private final StudyService studyService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    // bean
    private final EventValidator eventValidator;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String updateNewEventForm(@CurrentAccount Account account, @PathVariable String path, Errors errors, Model model, EventForm eventForm) {
        Study study = studyService.getStudyToUpdate(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/form";
        }
        // form data to model object
        Event event = eventService.createNewEvent(modelMapper.map(eventForm, Event.class), study, account);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

}
