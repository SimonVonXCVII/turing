package com.simonvonxcvii.turing.controller;

import com.simonvonxcvii.turing.common.exception.BizRuntimeException;
import com.simonvonxcvii.turing.common.result.Result;
import com.simonvonxcvii.turing.enums.FileTypeEnum;
import com.simonvonxcvii.turing.model.dto.UploadFileDTO;
import com.simonvonxcvii.turing.service.IAppFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * <p>
 * 文件表 前端控制器
 * </p>
 *
 * @author Simon Von
 * @since 2023-04-01 23:08:08
 */
@Tag(name = "AppFileController", description = "文件表 前端控制器")
@RestController
@RequestMapping("/api/appFile")
public class AppFileController {

    private final IAppFileService service;

    public AppFileController(IAppFileService service) {
        this.service = service;
    }

    @Operation(summary = "web 端上传文件")
    @PostMapping(value = "/webUploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<UploadFileDTO> webUploadFile(@Parameter(description = "文件") @RequestParam MultipartFile file,
                                               @Parameter(description = "文件业务类型") @RequestParam Integer bizType,
                                               @Parameter(description = "备注") String remark) throws IOException {
        // 检查文件
        CheckMultipartFile multipartFile = getCheckMultipartFile(file);
        // 限制可传文件格式
        if (!Pattern.matches("^\\.(?i)pdf$", multipartFile.suffix()) &&
                !Pattern.matches("^\\.(?i)docx?$", multipartFile.suffix()) &&
                !Pattern.matches("^\\.(?i)xlsx?$", multipartFile.suffix())) {
            throw BizRuntimeException.from("暂时只支持上传 .pdf、.doc、.docx、.xls 和 .xlsx 格式的文件");
        }
        return Result.ok(service.uploadFile(file, multipartFile.originalFilename(), multipartFile.suffix(), FileTypeEnum.getByOrdinal(bizType), remark, false, false));
    }

    @Operation(summary = "web 端上传图片")
    @PostMapping(value = "/webUploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<UploadFileDTO> webUploadImage(@Parameter(description = "文件") @RequestParam MultipartFile file,
                                                @Parameter(description = "文件业务类型") @RequestParam Integer bizType,
                                                @Parameter(description = "备注") String remark,
                                                @Parameter(description = "是否压缩") @RequestParam(required = false) Boolean isCompress) throws IOException {
        // 检查文件
        CheckMultipartFile multipartFile = getCheckMultipartFile(file);
        // 限制可传文件格式
        if (!Pattern.matches("^\\.(?i)jpe?g$", multipartFile.suffix()) &&
                !Pattern.matches("^\\.(?i)png$", multipartFile.suffix()) &&
                !Pattern.matches("^\\.(?i)webp$", multipartFile.suffix())) {
            throw BizRuntimeException.from("暂时只支持上传 .jpg、.jpeg、.png 和 .webp 格式的图片");
        }
        return Result.ok(service.uploadFile(file, multipartFile.originalFilename(), multipartFile.suffix(), FileTypeEnum.getByOrdinal(bizType), remark, isCompress, false));
    }

    @Operation(summary = "app 端上传文件")
    @PostMapping(value = "/appUploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<UploadFileDTO> appUploadFile(@Parameter(description = "文件") @RequestParam MultipartFile file,
                                               @Parameter(description = "文件业务类型") @RequestParam Integer bizType,
                                               @Parameter(description = "备注") String remark) throws IOException {
        // 检查文件
        CheckMultipartFile multipartFile = getCheckMultipartFile(file);
        // 限制可传文件格式
        if (!Pattern.matches("^\\.(?i)pdf$", multipartFile.suffix()) &&
                !Pattern.matches("^\\.(?i)docx?$", multipartFile.suffix()) &&
                !Pattern.matches("^\\.(?i)xlsx?$", multipartFile.suffix())) {
            throw BizRuntimeException.from("暂时只支持上传 .pdf、.doc、.docx、.xls 和 .xlsx 格式的文件");
        }
        return Result.ok(service.uploadFile(file, multipartFile.originalFilename(), multipartFile.suffix(), FileTypeEnum.getByOrdinal(bizType), remark, false, true));
    }

    @Operation(summary = "app 端上传图片")
    @PostMapping(value = "/appUploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<UploadFileDTO> appUploadImage(@Parameter(description = "文件") @RequestParam MultipartFile file,
                                                @Parameter(description = "文件业务类型") @RequestParam Integer bizType,
                                                @Parameter(description = "备注") String remark,
                                                @Parameter(description = "是否压缩") @RequestParam(required = false) Boolean isCompress) throws IOException {
        // 检查文件
        CheckMultipartFile multipartFile = getCheckMultipartFile(file);
        // 限制可传文件格式
        if (!Pattern.matches("^\\.(?i)jpe?g$", multipartFile.suffix()) &&
                !Pattern.matches("^\\.(?i)png$", multipartFile.suffix()) &&
                !Pattern.matches("^\\.(?i)webp$", multipartFile.suffix())) {
            throw BizRuntimeException.from("暂时只支持上传 .jpg、.jpeg、.png 和 .webp 格式的图片");
        }
        return Result.ok(service.uploadFile(file, multipartFile.originalFilename(), multipartFile.suffix(), FileTypeEnum.getByOrdinal(bizType), remark, isCompress, true));
    }

    @Parameter(name = "id", description = "文件 id")
    @Operation(summary = "根据文件 id 获取文件")
    @GetMapping(value = "/getFileById", produces = {MediaType.APPLICATION_PDF_VALUE, "application/msword", "application/vnd.ms-excel"})
    public void getFileById(@NotBlank(message = "id 不能为空") String id, HttpServletResponse response) throws IOException {
        service.getFileById(id, response);
    }

    @Parameter(name = "id", description = "文件 id")
    @Operation(summary = "根据图片文件 id 获取原始图片")
    @GetMapping(value = "/getOriginalImageById", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, "application/webp"})
    public void getOriginalImageById(@NotBlank(message = "id 不能为空") String id, HttpServletResponse response) throws IOException {
        service.getOriginalImageById(id, response);
    }

    @Parameter(name = "map", description = "Map 键值对形式的文件 id")
    @Operation(summary = "根据文件 id 删除文件")
    @DeleteMapping("/deleteById/{id}")
    public Result<Object> deleteById(@PathVariable String id) {
        service.deleteById(id);
        return Result.ok();
    }

    /**
     * 检查文件
     *
     * @param file 上传的文件
     * @return 原文件名和文件后缀
     * @author Simon Von
     * @since 2023/4/10 10:19
     */
    private CheckMultipartFile getCheckMultipartFile(MultipartFile file) {
        if (file == null) {
            throw BizRuntimeException.from("文件不可为空，请选择文件");
        }
        if (file.isEmpty()) {
            throw BizRuntimeException.from("没有选择文件或者选择的文件没有内容");
        }
        // 原文件名
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw BizRuntimeException.from("原文件名不能为空");
        }
        // 文件后缀 todo 使用 apache 或者 spring 中的 FileNameUtils 替换，还有 AppFileServiceImpl 中
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!StringUtils.hasText(suffix)) {
            throw BizRuntimeException.from("文件后缀不能为空");
        }
        return new CheckMultipartFile(originalFilename, suffix);
    }

    /**
     * 文件信息记录类
     *
     * @param originalFilename 原文件名
     * @param suffix           文件后缀
     * @author Simon Von
     * @since 2023/4/10 10:21
     */
    private record CheckMultipartFile(String originalFilename, String suffix) {
    }

}
