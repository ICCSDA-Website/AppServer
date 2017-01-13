package Server;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

	File[] lists;

	public void getUploads() {

		File folder = new File("/web/data");
		System.err.println(folder.getAbsolutePath());
		File[] list = folder.listFiles();
		lists = list;

	}

	@RequestMapping(value = { "/upload" }, method = { RequestMethod.POST })
	public String upload(@RequestParam("upload") MultipartFile file, HttpServletResponse response) throws IOException {

		File newfile = new File("/web/data/" + file.getOriginalFilename());

		InputStream in = file.getInputStream();
		BufferedInputStream bin = new BufferedInputStream(in);
		int read = 0;
		FileOutputStream out = new FileOutputStream(newfile);

		while ((read = bin.read()) != -1) {
			out.write(read);

		}
		out.flush();
		out.close();

		if (newfile.getName().contains(".jpg") || newfile.getName().contains(".png")
				|| newfile.getName().contains(".gif")) {

			compressedimage(newfile);
		}

		return "redirect:/";

	}

	private void compressedimage(File newfile) {
		try {
			System.out.println("compressed " + newfile.getName());
			BufferedImage image = ImageIO.read(newfile.getAbsoluteFile());
			File compressedImageFile = new File("/web/data/" + newfile.getName());
			OutputStream os = new FileOutputStream(compressedImageFile);
			Iterator<ImageWriter> writers = null;
			if (newfile.getName().contains("jpg")) {
				writers = ImageIO.getImageWritersByFormatName("jpg");
			} else if (newfile.getName().contains("png")) {
				writers = ImageIO.getImageWritersByFormatName("png");
			} else if (newfile.getName().contains("gif")) {
				writers = ImageIO.getImageWritersByFormatName("gif");
			}

			ImageWriter writer = (ImageWriter) writers.next();
			ImageOutputStream ios = ImageIO.createImageOutputStream(os);
			writer.setOutput(ios);
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(0.2f);

			writer.write(null, new IIOImage(image, null, null), param);

			os.close();
			ios.close();
			writer.dispose();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping(value = { "/uploads" }, method = { RequestMethod.GET })
	public ModelAndView getUpload(ModelAndView model, HttpServletRequest request, HttpServletResponse response) {
		model.setViewName("upload.html");
		return model;

	}

	@RequestMapping(value = { "/" }, method = { RequestMethod.GET })
	public ModelAndView getDomain(ModelAndView model, HttpServletRequest request, HttpServletResponse response) {
		getUploads();
		model.addObject("files", lists);
		model.setViewName("index.html");
		return model;
	}

}
