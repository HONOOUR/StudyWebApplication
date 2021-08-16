package com.studyolle.demo.study.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class StudyTitleForm {

    @NotBlank
    @Length(max = 50)
    private String title;

}
