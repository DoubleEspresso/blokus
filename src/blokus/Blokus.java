package blokus;

import graphics.Texture;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import com.sun.javafx.geom.Vec2d;

import static org.lwjgl.opengl.GL11.*;

public class Blokus implements Runnable{
	
	// variables
	public int EXIT_SUCCESS = 0, EXIT_FAILURE = -1;
	private Thread displayThread;
	public boolean running = true;
	private long window;	
	private int width = 1100, height = 500;
	private int mouseX,  mouseY, mouseDX , mouseDY ;
	private Texture texture = null;
	private Texture background = null;
	
	// check which blok we are dragging
	private Boolean dragging_b1 = false, dragging_b2 = false, dragging_b3 = false, dragging_b4 = false;
	
	// final mouse positions.
	private int mf_b1X, mf_b1Y, mf_b2X, mf_b2Y, mf_b3X, mf_b3Y, mf_b4X, mf_b4Y; 
	
	// rotation angles
	private float b1_theta, b2_theta, b3_theta, b4_theta;
	
	// flip booleans
	private Boolean flip_b4 = false;
	
    // error and event callback instances.
    //private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
    private GLFWMouseButtonCallback mouseCallback;
    private GLFWWindowSizeCallback resizeCallback;
    GLFWCursorPosCallback cursorPosCallback;
    GLFWScrollCallback scrollCallback;
    
	public static void main(String args[]){
		Blokus game = new Blokus();
		game.start();
	}
	
	
	public void start(){
		running = true;
		displayThread = new Thread(this);
		displayThread.start();
	}

	
	public void init(){
	
		// error callback .. default implementation
        // will print the error message in System.err.
        //glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
		
        // main initialization 
		if(glfwInit() != GL_TRUE){
			System.err.println("..[init] GLFW initialization failed, abort.");
			System.exit(EXIT_FAILURE);
		}
		
		// window configuration
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
		
		// create the game window
		window = glfwCreateWindow(width, height, "Blokus v1.0", NULL, NULL);

		if(window == NULL){
			System.err.println("..[init] failed to create GL window, abort.");
			System.exit(EXIT_FAILURE);
		}
		
		// key callback for main window 
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {           	
            	keyEventsCallback( window,  key,  scancode,  action,  mods);
            }
        });
        
        // mouse cursor position tracking callback
        // Initialize all mouse values as 0
        mouseX = mouseY = mouseDX = mouseDY = 0;

		glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback(){

            @Override
            public void invoke(long window, double xpos, double ypos) {
                // Add delta of x and y mouse coordinates
                mouseDX += (int)xpos - mouseX;
                mouseDY += (int)xpos - mouseY;
                // Set new positions of x and y
                mouseX = (int) xpos;
                mouseY = (int) ypos;
            }
        });
        
        // mouse button event callback
        glfwSetMouseButtonCallback(window, mouseCallback = new GLFWMouseButtonCallback() {
        	@Override
            public void invoke(long window, int button, int action, int mods) {
        		mouseCallback(window, button, action, mods);
            }
        });
        
        
		// the mouse scroll callback (used for rotating objects)
        glfwSetScrollCallback(window, scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
            	if (yoffset >= .1) 
            		{
            		  if (dragging_b1) b1_theta += 90;
            		  else if (dragging_b2) b2_theta += 90;
            		  else if (dragging_b3) b3_theta += 90;
            		  else if (dragging_b4) b4_theta += 90;            		  
            		}
            	else if (yoffset <= -.1)
            	{
          		  if (dragging_b1) b1_theta -= 90;
          		  else if (dragging_b2) b2_theta -= 90;
          		  else if (dragging_b3) b3_theta -= 90;
          		  else if (dragging_b4) b4_theta -= 90;
            	}
            }
        });
        
		// window resize callback
        glfwSetWindowSizeCallback(window, resizeCallback = new GLFWWindowSizeCallback() {
        	@Override
        	public void invoke(long window, int w, int h){
        		windowSizeCallback(window, w, h);
        		}
        });
                       
        // resolution/primary monitor setup
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        
        // centers the window
        glfwSetWindowPos( window, 
        		(vidmode.width() - width) / 2,
        		(vidmode.height() - height) / 2 );
        
        
        // make the OpenGL context current
        glfwMakeContextCurrent(window);
        
        // enable v-sync
        glfwSwapInterval(1);
 
        // make the window visible
        glfwShowWindow(window);
        
        
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        
        // enable 2d texturing
        glEnable(GL_TEXTURE_2D);
        
        // select a smooth shading model
        glShadeModel(GL_SMOOTH);
        
		// enable blending
		glEnable(GL_BLEND);
		
		// default blending function (?)
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		// enable alpha test - useful for images with transparent parts
		//glEnable(GL_ALPHA_TEST);
		//glAlphaFunc(GL_GREATER,0f);
		
		// default clear screen color is black
		//GL11.glClearColor(0.4f, 0.4f, 0.4f, 0.1f); 
		//glClearColor(0.2f, 0.2f, 0.2f, .3f);
		glClearColor(0.0f, 0.0f, 0.f, .0f);
		// Sets the size of the OpenGL viewport
		glViewport(0, 0, width, height);

		//if(background == null) background = new Texture( new String("C:\\code\\blokus\\src\\graphics\\texture\\background5_scaled.jpg") ) ;
		
		// private variables init .. 
		b1_theta = b2_theta = b3_theta = b4_theta = 0;

		// initial primitives positions
		mf_b1X = 100; mf_b1Y = 250;
		mf_b2X = 180; mf_b2Y = 250;
		mf_b3X = 260; mf_b3Y = 250;
		mf_b4X = 340; mf_b4Y = 250;
	}
	
	private void keyEventsCallback(long window, int key, int scancode, int action, int mods)
	{
		// close window if user presses escape .. detected during game loop
        if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) glfwSetWindowShouldClose(window, GL_TRUE);       

	
	}

	public int getDX() {
		// Return mouse delta x and set delta x to 0
		return mouseDX | (mouseDX = 0);
	}

	public int getDY() {
		// Return mouse delta y and set delta y to 0
		return mouseDY | (mouseDY = 0);
	}
	// mouse callback
	private void mouseCallback(long window, int button, int action, int mods)
	{
		if (action == GLFW_PRESS)
		{
			dragging_b1 = (Math.abs(mouseX - mf_b1X) <= 20 && Math.abs(mouseY - mf_b1Y) <= 20 );
			dragging_b2 = (Math.abs(mouseX - mf_b2X) <= 20 && Math.abs(mouseY - mf_b2Y) <= 20 );
			dragging_b3 = (Math.abs(mouseX - mf_b3X) <= 20 && Math.abs(mouseY - mf_b3Y) <= 20 );
			dragging_b4 = (Math.abs(mouseX - mf_b4X) <= 20 && Math.abs(mouseY - mf_b4Y) <= 20 );
			if (button == GLFW_MOUSE_BUTTON_RIGHT && dragging_b4) flip_b4 = !flip_b4;
		}
		else if (action == GLFW_RELEASE)
		{
			if (dragging_b1)
			{
				dragging_b1 = false; mf_b1X = mouseX; mf_b1Y = mouseY;
			}
			else if (dragging_b2)
			{
				dragging_b2 = false; mf_b2X = mouseX; mf_b2Y = mouseY;
			}
			else if (dragging_b3)
			{
				dragging_b3 = false; mf_b3X = mouseX; mf_b3Y = mouseY;
			}
			else if (dragging_b4)
			{
				dragging_b4 = false; mf_b4X = mouseX; mf_b4Y = mouseY;
			}
		}
		
		
	}
	
	// window resize callback .. this will fix objects in place if window
	// is resized 
	private void windowSizeCallback(long window, int w, int h) {
		
		GL.createCapabilities(); // necessary? else throws invoke exception 
		width = w; height = h;
		
		// Sets the size of the OpenGL viewport
		glViewport(0, 0, w, h);

		// select the projection stack and apply
		// an orthographic projection (keeps things in 2d)
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, w, h, 0.0, -1.0, 1.0);
		glMatrixMode(GL_MODELVIEW);
	}
	
	// update loop will poll for any window events
	public void doEventPolling(){
		
		glfwPollEvents();

	}
	
	// the main window render method
	public void render(){
		
	    if ( glfwWindowShouldClose(window) == GL_TRUE ) return;   

	    // step 1. clear framebuffer
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 

	    // step 1.5 load background
	    GL11.glLoadIdentity();
	    GL11.glTranslatef(width/2, height/2, 0.0f);
	    glColor4f(.2f, .8f, .8f, 1f);
	    //glBindTexture(GL_TEXTURE_2D, background.GetID());
	    glBegin(GL_QUADS);
	    //glTexCoord2f (0, 0); 
	    glVertex3i(-width/2,height/2,0);
	    //glTexCoord2f (1, 0); 
	    glVertex3i(width/2,height/2,0);
	    //glTexCoord2f (1, 1); 
	    glVertex3i(width/2,-height/2,0);
	    //glTexCoord2f (0, 1); 
	    glVertex3i(-width/2,-height/2,0);
	    glEnd();
	    
	    // step 2. test drawing simple primitives
	    GL11.glLoadIdentity();	
	    GL11.glTranslatef(width/2, height/2, 0.0f);
	    // step 3. draw board frame on the top of the background
	    glBegin(GL_QUADS);
	    //glTexCoord2f (0, 0);
	    GL11.glColor4f(0,1,0,0.2f); glVertex3i(-14*10,14*10,0);
	    //glTexCoord2f (1, 0);
	    GL11.glColor4f(0,1,0,0.2f); glVertex3i(14*10,14*10,0);
	    //glTexCoord2f (1, 1);
	    GL11.glColor4f(0,1,0,0.2f); glVertex3i(14*10,-14*10,0);
	    //glTexCoord2f (0, 1);
	    GL11.glColor4f(0,1,0,0.2f); glVertex3i(-14*10,-14*10,0);
	    glEnd();
	    
	    // example draw primitives (square + triangle) .. non-textured
	    // order of matrix multiplication .. M1 * M2 * ( object ) ...
	   
	    // blok 1
	    GL11.glLoadIdentity();	
	    getDX(); getDY();
	    if (dragging_b1) 
	    	GL11.glTranslatef(mouseX + getDX(), mouseY + getDY(), 0.0f); // M1
	    else GL11.glTranslatef(mf_b1X, mf_b1Y, 0.0f);
	    GL11.glRotatef(b1_theta, 0.0f, 0.0f, 1.0f); // M2	    	    
	    // draw square about the origin - makes translation/rotation simpler.
	    //glBindTexture(GL_TEXTURE_2D, texture.GetID());
	    glBegin(GL_QUADS);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-10,10,0);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(10,10,0);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(10,-10,0);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-10,-10,0);
	    glEnd();
	    
	    // blok 2
	    GL11.glLoadIdentity();	
	    getDX(); getDY();
	    if (dragging_b2) 
	    	GL11.glTranslatef(mouseX + getDX(), mouseY + getDY(), 0.0f); // M1
	    else GL11.glTranslatef(mf_b2X, mf_b2Y, 0.0f);
	    GL11.glRotatef(b2_theta, 0.0f, 0.0f, 1.0f); // M2	    	    
	    // draw square about the origin - makes translation/rotation simpler.
	    //glBindTexture(GL_TEXTURE_2D, texture.GetID());
	    glBegin(GL_QUADS);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-20,10,0);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(20,10,0);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(20,-10,0);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-20,-10,0);
	    glEnd();    
	    
	    // blok 3 - t-blok
	    GL11.glLoadIdentity();	
	    getDX(); getDY();
	    if (dragging_b3)
	    	GL11.glTranslatef(mouseX + getDX(), mouseY + getDY(), 0.0f); // M1
	    else GL11.glTranslatef(mf_b3X, mf_b3Y, 0.0f);
	    GL11.glRotatef(b3_theta, 0.0f, 0.0f, 1.0f); // M2	    	    

	    glBegin(GL_QUADS);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-30,10,0);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(30,10,0);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(30,-10,0);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-30,-10,0);
	    
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-10,30,0);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(10,30,0);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(10,10,0);
	    GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-10,10,0);
	    glEnd();
	    
	    // blok 4 - L-blok
	    gen_blok4(flip_b4);
	    
	    // step 3. finally swap buffers
        glfwSwapBuffers(window); 
	}
	
	// TODO: shorten this to one render function
	private Vec2d[] rounded_corner(Vec2d center, float radius, int nb_points, int type)
	{

		float start_theta = 0; float end_theta = 90; //defaults
		
		switch (type) {
		case 1: {
			start_theta = 0; end_theta = 90; // 1st quadrant of a circle;
			break;
		}
		case 2: {
			start_theta = 90; end_theta = 180; // 2nd quadrant of a circle;
			break;
		}
		case 3: {
			start_theta = 180; end_theta = 270; // 3rd quadrant of a circle;
			break;
		}
		case 4: {
			start_theta = 270; end_theta = 360; // 4th quadrant of a circle;
			break;
		}
		}
		
		float dtheta = (end_theta - start_theta) / nb_points;
		Vec2d[] points = new Vec2d[nb_points+1];
		points[0] = new Vec2d(center.x, center.y);
		
		
		// compute the points, and return a list of them to openGL
		for (int j = 1; j < nb_points+1; ++j) {
			double theta = (start_theta + j * dtheta) * Math.PI / 180.0;
			points[j] = new Vec2d(0,0);
			points[j].x = (float) (center.x + radius * Math.cos(theta));
			points[j].y = (float) (center.y + radius * Math.sin(theta));
		}
		
		return points;
	}
	
	private void render_rounded_corner(Vec2d center, float radius, int type)
	{
		int nb_points = 200; // default
		Vec2d[] points = rounded_corner( center,  radius,  nb_points, type);
		
		// default color will be white (alpha = 0) ??
		glBegin(GL_TRIANGLE_FAN);
		for (int j = 0; j < nb_points; ++j) {

			GL11.glColor4f(1, 1, 1, 1.0f); glVertex3f((float)points[j].x, (float)points[j].y, 0f);

		}
		glEnd();
	}
	
	private void gen_blok4(Boolean flipped)
	{		
		GL11.glLoadIdentity();
		getDX();
		getDY();
		if (dragging_b4) {
			GL11.glTranslatef(mouseX + getDX(), mouseY + getDY(), 0.0f); // M1

		} else
			GL11.glTranslatef(mf_b4X, mf_b4Y, 0.0f);
		GL11.glRotatef(b4_theta, 0.0f, 0.0f, 1.0f); // M2

		if (!flipped) {
			glBegin(GL_QUADS);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-30, 10, 0);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(30, 10, 0);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(30, -10, 0);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-30, -10, 0);

			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-30, 30, 0);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-10, 30, 0);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-10, 10, 0);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-30, 10, 0);
			glEnd();
			
			// TODO : rounded corners + better coloring
			
			// draw border -- traces around the piece starting from top-bottom
			// and moving counter-clockwise		
			glLineWidth(6.0f);
			glBegin(GL_LINES);
			GL11.glColor4f(1, 1, 1, 1.0f); glVertex3i(-30, -10+3, 0); 
			GL11.glColor4f(1, 1, 1, 1.0f); glVertex3i(-30, 30-3, 0); 
			
			GL11.glColor4f(1, 1, 1, 1.0f); glVertex3i(-30+3, -10, 0); 
			GL11.glColor4f(1, 1, 1, 1.0f); glVertex3i(30-3, -10, 0); 
			
			GL11.glColor4f(1, 1, 1, 1.0f); glVertex3i(30, 10-3, 0); 
			GL11.glColor4f(1, 1, 1, 1.0f); glVertex3i(30, -10+3, 0);
						
			GL11.glColor4f(1, 1, 1, 1.0f); glVertex3i(-10+3, 10, 0); 
			GL11.glColor4f(1, 1, 1, 1.0f); glVertex3i(30-3, 10, 0);  
			
			GL11.glColor4f(1, 1, 1, 1.0f); glVertex3i(-10, 10+3, 0); 
			GL11.glColor4f(1, 1, 1, 1.0f); glVertex3i(-10, 30-3, 0);
			
			GL11.glColor4f(1, 1, 1, 1.0f); glVertex3i(-10-3, 30, 0);
			GL11.glColor4f(1, 1, 1, 1.0f); glVertex3i(-30+3, 30, 0); 
			glEnd();		
			
			// (testing) - rounded corner for this blok -- most of 
			// the centers/radius combinations were found with a little trial and error .. 
			// .. better method here (?)
			float r = 6.0f;	    
		    render_rounded_corner(new Vec2d(-30+r/2,-10+r/2), r , 3); 		        
		    render_rounded_corner(new Vec2d(30-r/2,-10+r/2), r, 4);    
		    render_rounded_corner(new Vec2d(30-r/2, 10-r/2), r , 1);     
		    render_rounded_corner(new Vec2d(-10+r/2, 10+r/2), r , 3); 		    
		    render_rounded_corner(new Vec2d(-30+r/2, 30-r/2), r , 2); 		    
		    render_rounded_corner(new Vec2d(-10-r/2, 30-r/2), r , 1); 		
			glLineWidth(1.0f);
			
		} else {
			glBegin(GL_QUADS);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-30, 10, 0);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(30, 10, 0);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(30, -10, 0);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(-30, -10, 0);

			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(30, 30, 0);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(10, 30, 0);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(10, 10, 0);
			GL11.glColor4f(.9f,.4f,.4f,0.7f); glVertex3i(30, 10, 0);
			glEnd();
		}
	}
	
	@Override
	public void run() {
		try {
			init();

			// main game loop
			while (running) {
				doEventPolling();
				render();

				// the close event
				if (glfwWindowShouldClose(window) == GL_TRUE) {
					running = false;
				}
			}
		} finally {
			// terminate the GLFW and release any callbacks..
			glfwTerminate();
			keyCallback.release();
			resizeCallback.release();
			mouseCallback.release();
			cursorPosCallback.release();
			scrollCallback.release();
			//errorCallback.release();

		}
	}
}
