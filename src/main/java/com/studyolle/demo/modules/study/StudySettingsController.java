package com.studyolle.demo.modules.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.demo.modules.account.CurrentAccount;
import com.studyolle.demo.modules.account.Account;
import com.studyolle.demo.modules.tag.Tag;
import com.studyolle.demo.modules.zone.Zone;
import com.studyolle.demo.modules.zone.ZoneForm;
import com.studyolle.demo.modules.study.form.StudyDescriptionForm;
import com.studyolle.demo.modules.study.form.StudyPathForm;
import com.studyolle.demo.modules.tag.TagForm;
import com.studyolle.demo.modules.tag.TagRepository;
import com.studyolle.demo.modules.tag.TagService;
import com.studyolle.demo.modules.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingsController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final ZoneRepository zoneRepository;
    private final TagService tagService;

    @GetMapping("/description")
    public String viewStudySettings(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study, StudyDescriptionForm.class));
        return "study/settings/description";
    }

    @PostMapping("/description")
    public String updateStudyInfo(@CurrentAccount Account account, @PathVariable String path, @Valid StudyDescriptionForm studyDescriptionForm, Errors errors, Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "study/settings/description";
        }

        studyService.updateStudyDescription(study, studyDescriptionForm);
        attributes.addFlashAttribute("message", "????????? ????????? ??????????????????.");
        return "redirect:/study/" + study.getEncodedPath() + "/settings/description";
    }

    @GetMapping("/banner")
    public String viewStudyBanner(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/settings/banner";
    }

    @PostMapping("/banner")
    public String updateStudyBanner(@CurrentAccount Account account, @PathVariable String path, String image, Errors errors, Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.updateStudyBannerImage(study, image);
        attributes.addFlashAttribute("message", "????????? ?????? ???????????? ??????????????????.");
        return "redirect:/study/" + study.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String updateStudyBannerImageEnabled(@CurrentAccount Account account, @PathVariable String path, Errors errors, Model model, RedirectAttributes redirectAttributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.updateStudyBannerImageStatus(study);

        return "redirect:/study/" + study.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String updateStudyBannerImageDisabled(@CurrentAccount Account account, @PathVariable String path, Errors errors, Model model, RedirectAttributes redirectAttributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.updateStudyBannerImageStatus(study);

        return "redirect:/study/" + study.getEncodedPath() + "/settings/banner";
    }

    @GetMapping("/tags")
    public String viewStudyTags(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);

        Set<Tag> tags = studyService.getTags(study);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));

        return "study/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTags(@CurrentAccount Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdate(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        studyService.addTag(study, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity RemoveTags(@CurrentAccount Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdate(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.removeTag(study, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String viewStudyZone(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);

        Set<Zone> zones = studyService.getZones(study);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));

        return "study/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZones(@CurrentAccount Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
        Study study = studyService.getStudyToUpdate(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        studyService.addZone(study, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity RemoveZones(@CurrentAccount Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
        Study study = studyService.getStudyToUpdate(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.removeZone(study, zone);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/study")
    public String viewStudyStudy(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(study, StudyPathForm.class));
        return "study/settings/study";
    }

    @PostMapping("/study/close")
    public String updateStudyClose(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.close(study);
        attributes.addFlashAttribute("message", "???????????? ??????????????????.");
        return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
    }

    @PostMapping("/study/publish")
    public String updateStudyPublish(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.publish(study);
        attributes.addFlashAttribute("message", "???????????? ??????????????????.");
        return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
    }

    @PostMapping("/study/recruit/start")
    public String updateStudyRecruitStart(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.startRecruit(study);
        attributes.addFlashAttribute("message", "?????? ????????? ???????????????.");

        return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
    }

    @PostMapping("/study/recruit/stop")
    public String updateStudyRecruitStop(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.stopRecruit(study);
        attributes.addFlashAttribute("message", "?????? ????????? ???????????????..");

        return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
    }

    @PostMapping("/study/path")
    public String updateStudyPath(@CurrentAccount Account account, @PathVariable String path, Model model, @Valid StudyPathForm studyPathForm, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);;
        if (!studyService.isValidPath(studyPathForm.getPath())) {
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute("studyPathError", "?????? ????????? ????????? ????????? ??? ????????????.");
            return "study/settings/study";
        }
        studyService.updateStudyPath(study, studyPathForm);
        attributes.addFlashAttribute("message", "????????? ??????????????????.");

        return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
    }

    @PostMapping("/study/title")
    public String updateStudyTile(@CurrentAccount Account account, @PathVariable String path, Model model, @RequestParam String newTitle, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);;
        if (!studyService.isValidTitle(newTitle)) {
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute("studyTitleError", "?????? ????????? ????????? ????????? ??? ????????????.");
            return "study/settings/study";
        }
        studyService.updateStudyTitle(study, newTitle);
        attributes.addFlashAttribute("messge", "????????? ??????????????????.");

        return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
    }

    @PostMapping("/study/remove")
    public String removeStudy(@CurrentAccount Account account, @PathVariable String path, Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.removeStudy(study);
        attributes.addFlashAttribute("messge", "???????????? ??????????????????.");

        return "redirect:/";
    }
}
