package com.notebook.controller;

import com.notebook.domain.TagDo;
import com.notebook.service.TagService;
import com.notebook.util.ReturnCode;
import com.notebook.util.ReturnResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 标签表 前端控制器
 * </p>
 *
 * @author evan
 * @since 2020-11-05
 */
@RestController
@RequestMapping("/tag")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/list")
    public ReturnResult listTag() {
        List<TagDo> allTags = this.tagService.getAllTag();
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("tags", allTags);
    }

    @GetMapping("/get")
    public ReturnResult getTag(Integer tagId) {
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("tag", this.tagService.getTagById(tagId));
    }
}

