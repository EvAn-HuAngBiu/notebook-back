package com.notebook.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.config.NeedLogin;
import com.notebook.dao.mapper.CommentVoMapper;
import com.notebook.domain.CommentDo;
import com.notebook.domain.NotifyDo;
import com.notebook.domain.SwanRequestBody;
import com.notebook.domain.dto.SwanBriefCommentDto;
import com.notebook.domain.vo.ShareCommentVo;
import com.notebook.service.CommentService;
import com.notebook.service.NotifyService;
import com.notebook.service.ShareService;
import com.notebook.util.ReturnCode;
import com.notebook.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author evan
 * @since 2020-12-21
 */
@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final NotifyService notifyService;
    private final ShareService shareService;

    public CommentController(CommentService commentService, NotifyService notifyService,
                             ShareService shareService) {
        this.commentService = commentService;
        this.notifyService = notifyService;
        this.shareService = shareService;
    }

    @GetMapping("/list")
    public ReturnResult listShareComment(@RequestParam Integer shareId,
                                         @RequestParam(defaultValue = "0") Integer sortType) {
        return ReturnResult.newInstance().setCode(ReturnCode.SUCCESS)
                .putData("comments", commentService.getDetailCommentInfo(shareId, sortType));
    }

    @NeedLogin
    @PostMapping("/brief-reply")
    public ReturnResult briefReplyComment(@RequestBody SwanRequestBody<SwanBriefCommentDto> requestBody) {
        SwanBriefCommentDto data = requestBody.getData();
        CommentDo comment = new CommentDo(null, 0, data.getContent(), requestBody.getUserId(),
                data.getShareId(), null, LocalDateTime.now(), false, 0);
        boolean result = commentService.save(comment);
        if (result) {
            NotifyDo notifyDo = new NotifyDo(null, comment.getCommentId(),
                    shareService.getUserIdByShareId(data.getShareId()), false, LocalDateTime.now(), false, 0);
            if (!notifyService.save(notifyDo)) {
                log.error("Cannot write notify {}", notifyDo);
            }
        }
        return ReturnResult.newInstance().setCode(result ? ReturnCode.SUCCESS : ReturnCode.INTERNAL_ERROR);
    }

    @NeedLogin
    @PostMapping("/reply")
    public ReturnResult replyComment(@RequestBody SwanRequestBody<CommentDo> requestBody) {
        CommentDo comment  = requestBody.getData();
        comment.setUserId(requestBody.getUserId());
        comment.setAddTime(LocalDateTime.now());
        comment.setDeleted(false);
        comment.setVersion(0);
        boolean result = commentService.save(comment);
        if (result) {
            NotifyDo notifyDo = new NotifyDo(null, comment.getCommentId(),
                    shareService.getUserIdByShareId(comment.getShareId()), false, LocalDateTime.now(), false, 0);
            if (!notifyService.save(notifyDo)) {
                log.error("Cannot write notify {}", notifyDo);
            }
        }
        return ReturnResult.newInstance().setCode(result ? ReturnCode.SUCCESS : ReturnCode.INTERNAL_ERROR);
    }
}

