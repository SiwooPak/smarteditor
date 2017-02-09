package com.example;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Smarteditor;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
public class SmarteditorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmarteditorApplication.class, args);
	}

	@Controller
	@RequestMapping("/")
	class Editor {

		@GetMapping("/save")
		public String save() {
			return "/view";
		}

		@PostMapping("/save")
		public String save(Smarteditor smarteditor, Model model) {
			model.addAttribute("smarteditor", smarteditor);

			return "/view";
		}

		@GetMapping("/")
		public String fileUpload() {
			return "/smarteditor";
		}

		@PostMapping("/")
		public String fileUpload(Part part, HttpServletRequest request) throws IOException {
			// 실제 파일 이름
			String fileName = part.getSubmittedFileName();
			// 서버에 파일이 저장될 랜덤 이름
			String realFileNm = fileNameRandom(fileName);
			// 업로드 파일이 저장될 경로
			String uploadFilepath = "C:\\DevelopmentTools\\apache-tomcat-8.0.28\\webapps\\ROOT\\upload";

			fileExce(uploadFilepath);

			// 업로드할 파일이 있을 경우 실행 ( File.separatorChar : \\ 기능 )
			if (fileName.length() > 0) {
				part.write(uploadFilepath + File.separatorChar + realFileNm);
			}

			return redirectUri(request, realFileNm, "/upload/" + realFileNm);
		}

		// 디렉토리 없을 경우 디렉토리 생성
		private void fileExce(String uploadFilepath) {
			File file = new File(uploadFilepath);
			if (!file.exists()) {
				file.mkdirs();
			}
		}

		// callback.html 로 리다이렉트
		private String redirectUri(HttpServletRequest request, String realFileNm, String uploadFilepath) {
			String callback_uri = request.getParameter("callback");
			String callback_func = request.getParameter("callback_func");

			String redirectUri = String.format("redirect:%s?callback_func=%s&bNewLine=true&sFileName=%s&sFileURL=%s",
					callback_uri, callback_func, realFileNm, uploadFilepath);
			return redirectUri;
		}

		// 저장될 파일 이름 랜덤으로 생성
		private String fileNameRandom(String fileName) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			String today = formatter.format(new java.util.Date());
			String randomFileName = today + UUID.randomUUID().toString()
					+ fileName.substring(fileName.lastIndexOf("."));
			return randomFileName;
		}

	}

}
