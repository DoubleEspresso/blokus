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
	private Boolean IsMouseDrag = false;
	
	// testing variables
	private float square_angle;
	
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
            	if (yoffset >= .1) square_angle += 90;
            	else if (yoffset <= -.1) square_angle -= 90;
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
		glClearColor(0.2f, 0.2f, 0.2f, .3f);
		
		// Sets the size of the OpenGL viewport
		glViewport(0, 0, width, height);

		if(texture == null) texture = new Texture( new String("C:\\code\\blokus\\src\\graphics\\texture\\tiles.png") ) ;
		if(background == null) background = new Texture( new String("C:\\code\\blokus\\src\\graphics\\texture\\background5_scaled.jpg") ) ;
		
		// private variables init .. 
		square_angle = 0;

	}
	
	private void keyEventsCallback(long window, int key, int scancode, int action, int mods)
	{
		// close window if user presses escape .. detected during game loop
        if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) glfwSetWindowShouldClose(window, GL_TRUE);       
        else if (key == GLFW_KEY_RIGHT && action == GLFW_PRESS) 
        {
        	square_angle += 45 % 360;
        }
        	else if (key == GLFW_KEY_LEFT && action == GLFW_PRESS)
        {
			square_angle -= 45 % 360;
		}
	
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
		
		IsMouseDrag = (action == GLFW_PRESS);
		
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
	    glColor3f(1.0f, 1.0f, 1.0f);
	    glBindTexture(GL_TEXTURE_2D, background.GetID());
	    glBegin(GL_QUADS);
	    glTexCoord2f (0, 0);
	    glVertex3i(-width/2,height/2,0);
	    glTexCoord2f (1, 0);
	    glVertex3i(width/2,height/2,0);
	    glTexCoord2f (1, 1);
	    glVertex3i(width/2,-height/2,0);
	    glTexCoord2f (0, 1);
	    glVertex3i(-width/2,-height/2,0);
	    glEnd();
	    
	    
	    // step 2. test drawing simple primitives
	    GL11.glLoadIdentity();	
	    GL11.glTranslatef(width/2, height/2, 0.0f);
	    // step 3. draw board frame on the top of the background
	    glBegin(GL_QUADS);
	    //glTexCoord2f (0, 0);
	    GL11.glColor4f(1,1,0,0.7f); glVertex3i(-14*10,14*10,0);
	    //glTexCoord2f (1, 0);
	    GL11.glColor4f(1,1,0,0.7f); glVertex3i(14*10,14*10,0);
	    //glTexCoord2f (1, 1);
	    GL11.glColor4f(1,1,0,0.7f); glVertex3i(14*10,-14*10,0);
	    //glTexCoord2f (0, 1);
	    GL11.glColor4f(1,1,0,0.7f); glVertex3i(-14*10,-14*10,0);
	    glEnd();
	    
	    // example draw primitives (square + triangle) .. non-textured
	    // order of matrix multiplication .. M1 * M2 * ( object ) ...
	   
	    // blok 1
	    GL11.glLoadIdentity();	
	    getDX(); getDY();
	    if (IsMouseDrag) 
	    	GL11.glTranslatef(mouseX + getDX(), mouseY + getDY(), 0.0f); // M1
	    else GL11.glTranslatef(100, 250, 0.0f);
	    GL11.glRotatef(square_angle, 0.0f, 0.0f, 1.0f); // M2	    	    
	    // draw square about the origin - makes translation/rotation simpler.
	    //glBindTexture(GL_TEXTURE_2D, texture.GetID());
	    glBegin(GL_QUADS);
	    GL11.glColor4f(1,0,0,0.7f); glVertex3i(-10,10,0);
	    GL11.glColor4f(1,0,0,0.7f); glVertex3i(10,10,0);
	    GL11.glColor4f(1,0,0,0.7f); glVertex3i(10,-10,0);
	    GL11.glColor4f(1,0,0,0.7f); glVertex3i(-10,-10,0);
	    glEnd();
	    
	    // blok 2
	    GL11.glLoadIdentity();	
	    getDX(); getDY();
	    if (IsMouseDrag) 
	    	GL11.glTranslatef(mouseX + getDX(), mouseY + getDY(), 0.0f); // M1
	    else GL11.glTranslatef(180, 250, 0.0f);
	    GL11.glRotatef(square_angle, 0.0f, 0.0f, 1.0f); // M2	    	    
	    // draw square about the origin - makes translation/rotation simpler.
	    //glBindTexture(GL_TEXTURE_2D, texture.GetID());
	    glBegin(GL_QUADS);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(-20,10,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(20,10,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(20,-10,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(-20,-10,0);
	    glEnd();
	    
	    // blok 3 - t-blok
	    GL11.glLoadIdentity();	
	    getDX(); getDY();
	    if (IsMouseDrag) 
	    	GL11.glTranslatef(mouseX + getDX(), mouseY + getDY(), 0.0f); // M1
	    else GL11.glTranslatef(260, 250, 0.0f);
	    GL11.glRotatef(square_angle, 0.0f, 0.0f, 1.0f); // M2	    	    

	    glBegin(GL_QUADS);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(-30,10,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(30,10,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(30,-10,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(-30,-10,0);
	    
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(-10,30,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(10,30,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(10,10,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(-10,10,0);
	    glEnd();
	    
	    // blok 4 - L-blok
	    GL11.glLoadIdentity();	
	    getDX(); getDY();
	    if (IsMouseDrag) 
	    	GL11.glTranslatef(mouseX + getDX(), mouseY + getDY(), 0.0f); // M1
	    else GL11.glTranslatef(340, 250, 0.0f);
	    GL11.glRotatef(square_angle, 0.0f, 0.0f, 1.0f); // M2	    	    

	    glBegin(GL_QUADS);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(-30,10,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(30,10,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(30,-10,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(-30,-10,0);
	    
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(-30,30,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(-10,30,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(-10,10,0);
	    GL11.glColor4f(0,1,0,0.7f); glVertex3i(-30,10,0);
	    glEnd();
	    
	    // step 3. finally swap buffers
        glfwSwapBuffers(window); 
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
