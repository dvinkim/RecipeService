package kim.vincent.recipe;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RecipeController {
	
	@GetMapping("/")
	public String rootPage(Model model) {
		// Return index.html
		return "index";
	}
	
	@PostMapping("/")
	public String sendEmail(Model model,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "vegetarian", required = true) String v,
			@RequestParam(value = "people", required = true) String p,
			@RequestParam(value = "time", required = true) String t,
			@RequestParam(value = "email", required = true) String email) {
		
		// Create recipe
		ArrayList<String> res = getRecipe(v, p ,t);
		
		// Create HTML string
		StringBuilder result = new StringBuilder();
		result.append("<img src=\"banner.png\"></img>");
		for (String text : res) {
			result.append("<p>");
			result.append(text);
			result.append("</p>");
		}
		
		// Attach results to model
		model.addAttribute("result", result.toString());
		model.addAttribute("name", name);
		model.addAttribute("email", email);
		
		// Create pdf and send email
		if (createPDF(res)) {
			sendMail(email, result.toString());
		}
		
		// Return emailed.html
		return "emailed";
	}
	
	/**
	 * Creates response body based on parameters
	 * 
	 * @param v
	 * @param p
	 * @param t
	 * @return
	 */
	ArrayList<String> getRecipe(String v, String p, String t) {
		ArrayList<String> res = new ArrayList<>();
		
		// Header
		if (v.equals("a") && !t.equals("a")) {
			res.add("Healthy vegetarian options for you!");
		} else if (t.equals("a")) {
			res.add("Let's make something quick:");
		} else {
			res.add("We've got a recipe for you:");
		}
		
		// Body 1
		if (v.equals("b") && t.equals("c")) {
			res.add("World's best Lasagna");
		} else if (v.equals("a")) {
			res.add("Tortellini with basil");
		} else {
			res.add("Busy spaghetti");
		}
		
		// Body 2
		if (t.equals("a")) {
			res.add("We know you don't have a lot of time, so we created this recipe for you.");
		} else if (t.equals("b")) {
			res.add("Remember to keep an eye on the clock - this should take you roughly half an hour.");
		} else {
			res.add("You've got all the time in the world - enjoy the process!");
		}
		
		// Body 3
		if (p.equals("c")) {
			res.add("Food is a labor of love - your family will appreciate it!");
		} else if (p.equals("a") && t.equals("a")) {
			res.add("Hopefully we saved you some time - we know you're busy!");
		} else {
			res.add("Enjoy your meal!");
		}
		
		return res;
	}
	
	/**
	 * Creates PDF with the response
	 * 
	 * @param res
	 * @return True if successful
	 */
	private boolean createPDF(ArrayList<String> res) {
		try {
			System.out.println("Creating PDF...");
			
			// Create PDF
			PDDocument document = new PDDocument();
			PDPage page = new PDPage();
			document.addPage(page);
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			
			// Load Banner image
			PDImageXObject image = PDImageXObject.createFromFile("src/main/resources/static/banner.png", document);
			
			// Draw image on top
			contentStream.drawImage(image, 25, 650, 500, 114);
			
			// Set place for text
			contentStream.setFont(PDType1Font.HELVETICA, 12);
			contentStream.setLeading(14.5f);
			contentStream.beginText();
			contentStream.newLineAtOffset(25, 635);
			
			// Print text
			for (String r : res) {
				contentStream.showText(r);
				contentStream.newLine();
			}
			
			// Finish
			contentStream.endText();
			contentStream.close();
			document.save("Recipe.pdf");
			document.close();
			
			// Set to delete on exit
			new File("Recipe.pdf").deleteOnExit();
		
			System.out.println("PDF created successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void sendMail(String to, String htmlBody) {
		try {
			
			// Load mail server credentials from properties file
			Properties props = new Properties();
			props.load(new FileInputStream("mailcredentials.properties"));
			String username = props.getProperty("username");
			String password = props.getProperty("password");
	
			// Set session with credentials
			Session session = Session.getDefaultInstance(props,
					  new Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username, password);
						}
					  });
			 MimeMessage message = new MimeMessage(session);

			 // Set header fields
			 message.setFrom(new InternetAddress(username));
			 message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			 message.setSubject("Here is your recipe!");
	         
	         // Create message
	         MimeMultipart multipart = new MimeMultipart("related");
	         MimeBodyPart bodyPart = new MimeBodyPart();
	         
	         // Create HTML body
	         bodyPart.setText(htmlBody.replace("banner.png", "cid:banner"), "US-ASCII", "html");
	         multipart.addBodyPart(bodyPart);

	         // Attach image
	         bodyPart = new MimeBodyPart();
	         bodyPart.attachFile("src/main/resources/static/banner.png");
	         bodyPart.setContentID("<banner>");
	         bodyPart.setDisposition(MimeBodyPart.INLINE);
	         multipart.addBodyPart(bodyPart);

	         // Attach PDF
	         bodyPart = new MimeBodyPart();
	         String filename = "Recipe.pdf";
	         DataSource source = new FileDataSource(filename);
	         bodyPart.setDataHandler(new DataHandler(source));
	         bodyPart.setFileName(filename);
	         multipart.addBodyPart(bodyPart);

	         // Complete and send message
	         message.setContent(multipart);
			 System.out.println("Sending message....");
			 Transport.send(message);
			 System.out.println("Message sent successfully.");
			 
			 // Delete PDF
			new File("Recipe.pdf").delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
