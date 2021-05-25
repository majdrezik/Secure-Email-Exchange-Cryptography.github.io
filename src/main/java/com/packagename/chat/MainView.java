package com.packagename.chat;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
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
import com.vaadin.flow.server.VaadinRequest;

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
@PWA(name = "CryptoEmail",
        shortName = "CryptoEmail",
        description = "This is a simple Email Exchange App.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@StyleSheet("frontend://styles/shared-styles.css")
public class MainView extends VerticalLayout {

	

	private UnicastProcessor<ChatMessage> publisher;
	private Flux<ChatMessage> messages;
    public String _from, _to, _cc, _subject, _body, username;
	User user1 = new User("user1","123");
	User user2 = new User("user2","123");
    
    
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    
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

	
 
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    
    
    private void askUsername() {
		HorizontalLayout usernameLayout = new HorizontalLayout();

		
		
		LoginForm component = new LoginForm();
		component.addLoginListener(e -> {
		username = e.getUsername();
		String password = e.getPassword();
		boolean isUserFound, isPasswordRight =false, isAuthenticated;
	   isUserFound = username.equalsIgnoreCase(user1.getUserName()) || username.equalsIgnoreCase(user2.getUserName()) ? true : false;
	   
	   if(isUserFound) {
		   if(username.equals(user1.getUserName())) {
			   isPasswordRight = password.equalsIgnoreCase(user1.getPassword()) ? true : false;
		   } else {
			   isPasswordRight = password.equalsIgnoreCase(user2.getPassword()) ? true : false;
		   }
	   }
	   isAuthenticated = isUserFound && isPasswordRight;
	    if (isAuthenticated) {
			remove(usernameLayout);
	        showChat();
	    } else {
	        component.setError(true);
	    }
		});

		add(component);
		
		usernameLayout.add(component);
		add(usernameLayout);
	}

    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    
	private void showChat() {
		MessageList messageList = new MessageList();
		add(messageList,createInputLayout());
		expand(messageList);
		
				messages.subscribe(message -> {
			getUI().ifPresent(ui -> 
				ui.access(()->	//The thing that takes in the runnable
					messageList.add("\n" + 
							"From : " + message.getFrom() + "  ||  To : " + message.getTo() + "  ||  Cc : "+ message.getCc() + "  ||  Subject : " + message.getSubject() + "  ||  Body : "+ message.getBody()
							)
					));
		});
		
	}

	/**
	 * @return
	 */
	private Component createInputLayout() {
		HorizontalLayout inputLayout = new HorizontalLayout();
		inputLayout.setWidth("50%");
		



		TextField to = new TextField("To");
		TextField cc = new TextField("Cc");
		TextField subject = new TextField("Subject");
		TextField body = new TextField("Body");

		VerticalLayout vertical = new VerticalLayout();
		

		

		
		to.setPlaceholder("To");
		cc.setPlaceholder("Cc");
		subject.setPlaceholder("Subject");
		body.setPlaceholder("Body");

		Button sendButton = new Button("Send");
		sendButton.getElement().getThemeList().add("primary");
		

        vertical.add(to);
        vertical.add(cc);
        vertical.add(subject);
        vertical.add(body);
        vertical.add(sendButton);
        
        vertical.setHorizontalComponentAlignment(Alignment.STRETCH , vertical);
        vertical.setHorizontalComponentAlignment(Alignment.CENTER, vertical);;

        inputLayout.add(vertical);
		inputLayout.expand(body);
		
		to.focus();
		
		sendButton.addClickListener(click -> {
			
			_from = "\n" + username + "\n";
			_to = to.getValue() + "\n";
			_cc = cc.getValue ()+ "\n";
			_subject = subject.getValue() + "\n";
			_body = body.getValue() + "\n";
			
			boolean isUserValid;
			
			
			isUserValid = _to.equalsIgnoreCase(user1.getUserName()) || _to.equalsIgnoreCase(user2.getUserName()) ? true : false;
			
			//if the reciever-user is not found, show a dialog box and dont show the email in the sent emails.
			if(!isUserValid) {
				Dialog dialog = new Dialog();
				dialog.add(new Text("The user you are trying to reach, is not found.\n Close me with the esc-key or an outside click"));
				dialog.setWidth("400px");
				dialog.setHeight("150px");
				dialog.open();
				body.clear(); //clear the message field and get back the placeholder.
				to.clear();
				cc.clear();
				subject.clear();
				to.focus(); //focus on message field so the user can continue typing.
			}
			else{
				publisher.onNext(new ChatMessage(_from, _to, _cc,_subject,_body));
				body.clear(); //clear the message field and get back the placeholder.
				to.clear();
				cc.clear();
				subject.clear();
				to.focus(); //focus on message field so the user can continue typing.
			}
		});
		to.focus(); //focus on message field so the user can continue typing.

		return inputLayout;
	}
	
	
}
