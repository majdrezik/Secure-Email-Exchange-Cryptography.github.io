package com.packagename.chat;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.LoadingIndicatorConfiguration;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public String _from, _to, _cc, _subject, _body, username, time;
	User user1 = new User("user1","123");
	User user2 = new User("user2","123");
    HorizontalLayout mainLayout = new HorizontalLayout();

    
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    
    public MainView(UnicastProcessor<ChatMessage> publisher, Flux<ChatMessage> messages) {
    	this.publisher = publisher;
    	this.messages = messages;
    	mainLayout.setWidth("100%");
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
		   if(username.equalsIgnoreCase(user1.getUserName())) {
			   isPasswordRight = password.equalsIgnoreCase(user1.getPassword()) ? true : false;
		   } else {
			   isPasswordRight = password.equalsIgnoreCase(user2.getPassword()) ? true : false;
		   }
	   }
	   isAuthenticated = isUserFound && isPasswordRight;
	    if (isAuthenticated) {
			remove(usernameLayout);
			//set progressBar 
			ProgressBar progressBar = new ProgressBar();
			progressBar.setIndeterminate(true);
			add(progressBar);	
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			remove(progressBar);
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
    
    
    String getTime() {
    	SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
    	Date date = new Date(System.currentTimeMillis());
    	return formatter.format(date);
    }

    
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    
	private void showChat() {
		
		HorizontalLayout inputLayout = new HorizontalLayout();

		Button logOutButton = new Button("Log out");
		logOutButton.getElement().getThemeList().add("primary");
		
		logOutButton.setWidth("10%");
		
				
		MessageList messageList = new MessageList();
		messageList.setWidth("100%");
		messageList.setHeight("100%");
	//	mainLayout.add(messageList);

		
		//inputLayout.add(messageList,createInputLayout(),logOutButton);
		//add(inputLayout);
		expand(messageList); 
		
				messages.subscribe(message -> {
			getUI().ifPresent(ui -> 
				ui.access(()->	//The thing that takes in the runnable
					messageList.add(new Paragraph( getTime() + 
							"  ||  From : " + message.getFrom() + "  ||  To : " + message.getTo() + 
							"  ||  Cc : "+ message.getCc() + "  ||  Subject : " + message.getSubject() + 
							"  ||  Body : " + message.getBody() 
							))
					));
		});	
				
		logOutButton.addClickListener(click -> {
			remove(mainLayout);
			mainLayout.removeAll();
			askUsername();
		});
		
		//inputLayout.add(messageList);
		//mainLayout.add(inputLayout	);
		mainLayout.add(messageList,createInputLayout(),logOutButton);
		add(mainLayout);

	}

	
	   
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	
	
	private Component createInputLayout() {
		HorizontalLayout inputLayout = new HorizontalLayout();
		inputLayout.setWidth("50%"); 
		inputLayout.setHeight("100%");

		

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
		
		
		to.setWidth("100%");
		cc.setWidth("100%");
		subject.setWidth("100%");
		body.setWidth("100%");
		
        vertical.add(to);
        vertical.add(cc);
        vertical.add(subject);
        vertical.add(body);
        vertical.add(sendButton);
        
       // vertical.setHorizontalComponentAlignment(Alignment.STRETCH , vertical);
       // vertical.setHorizontalComponentAlignment(Alignment.CENTER, vertical);;
        vertical.setWidth("100%");
        inputLayout.add(vertical);
		//inputLayout.expand(body);
        
		//inputLayout.setWidth("60px");
		to.focus();
		
		sendButton.addClickListener(click -> {
			
			_from = username;
			_to = to.getValue();
			_cc = cc.getValue ()+ "\n";
			_subject = subject.getValue() + "\n";
			_body = body.getValue() + "\n";

			boolean isUserValid = false, isPopUp=false;
			
			
			//isUserValid = _to.equalsIgnoreCase(user1.getUserName()) || _to.equalsIgnoreCase(user2.getUserName()) ? true : false;
		//	isUserValid = _to.contains("user") ? true : false;
			String errorMessage = "' " + _to + " ' is unreachable.";
			if(_from.equalsIgnoreCase(user1.getUserName())) {
				if(_to.equalsIgnoreCase(user2.getUserName())) {
					isUserValid = true;
				}else {
					errorMessage += "You can only send to: ' " + user2.getUserName() + " '. Close me with the esc-key or an outside click";	
				}
			}else if(_from.equalsIgnoreCase(user2.getUserName())) {
					if(_to.equalsIgnoreCase(user1.getUserName())) {
						isUserValid = true;
					} else {
						errorMessage += "You can only send to: '" + user1.getUserName() + " '. Close me with the esc-key or an outside click";
					}
			}
			
			if(!isUserValid) {
				isPopUp = true;
				Dialog dialog = new Dialog();
				dialog.add(new Text(errorMessage));
				dialog.setWidth("400px");
				dialog.setHeight("150px");
				dialog.open();
			}
	
			
			if((_to.isEmpty() || _to.equals(" ") || _to.equals(null)) && !isPopUp) {
				Dialog dialog = new Dialog();
				dialog.add(new Text("*To* Field can't be empty.					\n Close me with the esc-key or an outside click"));
				dialog.setWidth("400px");
				dialog.setHeight("150px");
				dialog.open();
			}
			
			

			
			if(isUserValid) {
				publisher.onNext(new ChatMessage(_from, _to, _cc,_subject,_body));
				//clear fields
				body.clear(); //clear the message field and get back the placeholder.
				to.clear();
				cc.clear();
				subject.clear();
				to.focus(); //focus on message field so the user can continue typing.
				
			}
			
			/*else {
				if(!isPopUp) {
					Dialog dialog = new Dialog();
					String error = "' " + _to + " ' is unreachable.\n Close me with the esc-key or an outside click";
					dialog.add(error);
					dialog.setWidth("400px");
					dialog.setHeight("150px");
					dialog.open();
					
					//clear fields
					body.clear(); //clear the message field and get back the placeholder.
					to.clear();
					cc.clear();
					subject.clear();
					to.focus(); //focus on message field so the user can continue typing.
				}
			}*/
			
		});
			
		to.focus(); //focus on message field so the user can continue typing.
		//add(inputLayout);
		return inputLayout;
	}

	
	
}
	
	
	
	
	
	   
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////						THEME 2
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/*
	private Component createInputLayout() {
	
		HorizontalLayout inputLayout = new HorizontalLayout();
		inputLayout.setWidth("100%");
		inputLayout.setHeight("100%");

		//inputLayout.setMargin(true);
		layout.add(inputLayout);

		//added new theme

		List<ChatMessage> chatList = new ArrayList<>();

		chatList.add(new ChatMessage("from1", "Lucas", "Kane", "subject1","body1"));
		chatList.add(new ChatMessage("from2", "Lucas", "Kane2", "subject2","body2"));
		chatList.add(new ChatMessage("from3", "Lucas", "Kane3", "subject3","body3"));
		chatList.add(new ChatMessage("from4", "Lucas", "Kane4", "subject4","body4"));

		

		Grid<ChatMessage> grid = new Grid<>(ChatMessage.class);
		grid.setItems(chatList);

		//grid.removeColumnByKey("id");

		// The Grid<>(Person.class) sorts the properties and in order to
		// reorder the properties we use the 'setColumns' method.
		grid.setColumns("From", "To", "Cc", "Subject", "Body");
		add(grid);
		inputLayout.add(grid);

		//
	
		
		return inputLayout;
	}

	
	
	private void showChat() {

		MessageList messageList = new MessageList();
		layout.setWidth("100%");
		messageList.setWidth("70%");
		messageList.setHeight("100%");
		layout.add(messageList);
		add(messageList,createInputLayout());
		expand(messageList);
		
		HorizontalLayout inputLayout = new HorizontalLayout();
		inputLayout.setWidth("100%");
		inputLayout.setHeight("100%");

		//inputLayout.setMargin(true);
		layout.add(inputLayout);

		//added new theme

		List<ChatMessage> chatList = new ArrayList<>();

		chatList.add(new ChatMessage("from1", "Lucas", "Kane", "subject1","body1"));
		chatList.add(new ChatMessage("from2", "Lucas", "Kane2", "subject2","body2"));
		chatList.add(new ChatMessage("from3", "Lucas", "Kane3", "subject3","body3"));
		chatList.add(new ChatMessage("from4", "Lucas", "Kane4", "subject4","body4"));

		

		Grid<ChatMessage> grid = new Grid<>(ChatMessage.class);
		grid.setItems(chatList);
		grid.setColumns("From", "To", "Cc", "Subject", "Body");
		add(grid);
		inputLayout.add(grid);
		
				/*messages.subscribe(message -> {
			getUI().ifPresent(ui -> 
				ui.access(()->	//The thing that takes in the runnable
					messageList.add(grid)
					));
		});	*/
				
	
