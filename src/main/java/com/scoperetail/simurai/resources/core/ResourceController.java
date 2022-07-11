package com.scoperetail.simurai.resources.core;

/*-
 * *****
 * simurai-resources-core
 * -----
 * Copyright (C) 2018 - 2022 Scope Retail Systems Inc.
 * -----
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * =====
 */

import static org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ResourceController {
  private static final String ZIP_EXTENSION = ".zip";
  private static final String SOURCES_DIR = "SOURCES";
  @Autowired private ResourceLoader resourceLoader;

  @CrossOrigin
  @GetMapping(value = "/resources")
  public ResponseEntity<String> getResources(
      @RequestParam(value = "zipFilename", required = false, defaultValue = SOURCES_DIR)
          final String zipFilename,
      @RequestParam(value = "appName") final String appName,
      final HttpServletResponse response)
      throws IOException {
    log.info("Request received to fetch the resources for {}", appName);
    final Resource resource = resourceLoader.getResource(CLASSPATH_URL_PREFIX + appName);
    ResponseEntity<String> responseEntity = null;
    if (resource.exists()) {
      response.setContentType("application/zip");
      response.setHeader(
          CONTENT_DISPOSITION, "attachment; filename=" + zipFilename + ZIP_EXTENSION);
      ZipUtil.pack(resource.getFile(), response.getOutputStream());
      responseEntity = ResponseEntity.ok().build();
    } else {
      log.error("No resources configured for {}", appName);
      responseEntity = ResponseEntity.badRequest().body("No resources configured for " + appName);
    }
    return responseEntity;
  }
}
