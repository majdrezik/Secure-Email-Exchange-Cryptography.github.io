package com.packagename.chat;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@SuppressWarnings("serial")
@Route("")
@Push
@PWA(name = "MajdChat",
        shortName = "MajdChat",
        description = "This is a simple chatroom.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@StyleSheet("frontend://styles/shared-styles.css")
public class MainView extends VerticalLayout {

	private String username;
	private UnicastProcessor<ChatMessage> publisher;
	private Flux<ChatMessage> messages;
    
    public MainView(UnicastProcessor<ChatMessage> publisher, Flux<ChatMessage> messages) {
    	this.publisher = publisher;
    	this.messages = messages;
    	setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    	setSizeFull(); 
    	addClassName("main-view");
    	H1 header = new H1("CryptoEmail");
    	header.getElement().getThemeList().add("dark");
    	add(header);
    	askUsername();
    }

	
    
    
    
    
    
    
    private void askUsername() {
		HorizontalLayout usernameLayout = new HorizontalLayout();
		User user = new User("majd_rezik","majd123");

		// NEW LOGIN
		
		LoginForm component = new LoginForm();
		component.addLoginListener(e -> {
			String username = e.getUsername();
			String password = e.getPassword();
		    boolean isAuthenticated = username.equals(user.getUserName()) && password.equals(user.getPassword());
		    if (isAuthenticated) {
				remove(usernameLayout);
		        showChat();
		    } else {
		        component.setError(true);
		    }
		});

		add(component);
		
		usernameLayout.add(component);
		

		//
		
	
		/*
		TextField usernameField = new TextField();
		PasswordField password = new PasswordField("Password");
		
		usernameField.setPlaceholder("Username");
		password.setPlaceholder("Password");
		
		usernameField.focus();
		Button startButton = new Button("Login");
		
		usernameField.setRequired(true);
		password.setRequired(true);

		usernameLayout.add(usernameField);
		usernameLayout.add(password);
		usernameLayout.add(startButton);
		
		startButton.addClickListener(click -> {
			username = usernameField.getValue();
			User majd = new User("majd_rezik","majd123");
			if(usernameField.getValue().equals(majd.getUserName()) && password.getValue().equals(majd.getPassword())) {
				remove(usernameLayout);
				showChat();
			}else {
				
			}
		});
		
		*/
		add(usernameLayout);
	}

	private void showChat() {
		MessageList messageList = new MessageList();
		add(messageList,createInputLayout());
		expand(messageList);
		
		//TO ENABLE MULTI USERS AT THE SAME TIME.
		//lock the UI
		/*
		messages.subscribe(message -> {
			getUI().ifPresent(ui -> 
				ui.access(()->	//The thing that takes in the runnable
					messageList.add(new Paragraph(
							message.getFrom() + ": " + message.getMessage()
							))
					));
		});
		*/
	}

	/**
	 * @return
	 */
	private Component createInputLayout() {
		HorizontalLayout inputLayout = new HorizontalLayout();
		inputLayout.setWidth("100%");
		
		//TextField messageField = new TextField();

		TextField from = new TextField("From");
		TextField to = new TextField("To");
		TextField cc = new TextField("Cc");
		TextField subject = new TextField("Subject");
		TextField body = new TextField("Body");

		VerticalLayout vertical = new VerticalLayout();
		

		
		from.setPlaceholder("From");
		
		to.setPlaceholder("To");
		cc.setPlaceholder("Cc");
		subject.setPlaceholder("Subject");
		body.setPlaceholder("Body");

		Button sendButton = new Button("Send");
		sendButton.getElement().getThemeList().add("primary");
		
        vertical.add(from);
        vertical.add(to);
        vertical.add(cc);
        vertical.add(subject);
        vertical.add(body);
        vertical.add(sendButton);
        
        vertical.setHorizontalComponentAlignment(Alignment.STRETCH , vertical);
        vertical.setHorizontalComponentAlignment(Alignment.CENTER, vertical);;

        inputLayout.add(vertical);
		inputLayout.expand(body);
		
		from.focus();
		
		sendButton.addClickListener(click -> {
			publisher.onNext(new ChatMessage(username, body.getValue()));
			body.clear(); //clear the message field and get back the placeholder.
			to.clear();
			cc.clear();
			subject.clear();
			to.focus(); //focus on message field so the user can continue typing.
		});
		to.focus(); //focus on message field so the user can continue typing.

		return inputLayout;
	}
}
