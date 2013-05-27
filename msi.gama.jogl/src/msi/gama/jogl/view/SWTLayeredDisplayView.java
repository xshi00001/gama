package msi.gama.jogl.view;

import java.awt.Color;

import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.glu.GLU;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;


import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.perspectives.ModelingPerspective;
import msi.gama.gui.views.LayeredDisplayView;
import msi.gama.gui.views.SWTNavigationPanel;
import msi.gama.jogl.JOGLSWTDisplaySurface;
import msi.gama.outputs.LayeredDisplayOutput;

import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;

public class SWTLayeredDisplayView extends LayeredDisplayView {

	public static final String ID = GuiUtils.SWT_LAYER_VIEW_ID;
	
	@Override
	public void ownCreatePartControl(final Composite c) {
		super.ownCreatePartControl(c);
		
		surfaceCompo  = createSurfaceCompo();
		
//		
//		surfaceCompo = new Composite(parent, SWT.NO_BACKGROUND);
//		surfaceCompo.setLayout( new FillLayout() );

//        GLData gldata = new GLData();
//        gldata.doubleBuffer = true;
//        // need SWT.NO_BACKGROUND to prevent SWT from clearing the window
//        // at the wrong times (we use glClear for this instead)
//        final GLCanvas glcanvas = new GLCanvas( surfaceCompo, SWT.NO_BACKGROUND, gldata );
//        glcanvas.setCurrent();
//        final GLContext glcontext = GLDrawableFactory.getFactory().createExternalGLContext();
//
//        // fix the viewport when the user resizes the window
//        glcanvas.addListener( SWT.Resize, new Listener() {
//            public void handleEvent(Event event) {
//                Rectangle rectangle = glcanvas.getClientArea();
//                glcanvas.setCurrent();
//                glcontext.makeCurrent();
//                setup( glcontext.getGL(), rectangle.width, rectangle.height );
//                glcontext.release();        
//            }
//        });
//
//        // draw the triangle when the OS tells us that any part of the window needs drawing
//        glcanvas.addPaintListener( new PaintListener() {
//            public void paintControl( PaintEvent paintevent ) {
//                Rectangle rectangle = glcanvas.getClientArea();
//                glcanvas.setCurrent();
//                glcontext.makeCurrent();
//                render(glcontext.getGL(), rectangle.width, rectangle.height);
//                glcanvas.swapBuffers();
//                glcontext.release();        
//            }
//        });
		
		aux = new SWTNavigationPanel(general, SWT.None, getOutput().getSurface());
		data = new GridData(SWT.CENTER, SWT.FILL, true, true);
		data.minimumHeight = 200;
		data.heightHint = 200;
		data.widthHint = 200;
		data.horizontalSpan = 2;
		aux.setLayoutData(data);
		
		EditorFactory.create(general, "Color:", output.getBackgroundColor(), new EditorListener<Color>() {
			
			@Override
			public void valueModified(final Color newValue) {
				output.setBackgroundColor(newValue);
			}
		});
		createItem("Navigation", null, general, true);
		displayItems();
		getOutput().getSurface().setZoomListener(this);
		((SashForm) parent).setWeights(new int[] { 1, 2 });
		((SashForm) parent).setMaximizedControl(surfaceCompo);
	}
	
	public Composite createSurfaceCompo(){
		Composite c = new Composite(parent, SWT.EMBEDDED);
		c.setLayout(new FillLayout());
		createSurface(c);
		
		perspectiveListener = new IPerspectiveListener() {

			boolean previousState = false;

			@Override
			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {}

			@Override
			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
				if ( perspective.getId().equals(ModelingPerspective.ID) ) {
					if ( getOutput() != null && getOutput().getSurface() != null ) {
						previousState = getOutput().getSurface().isPaused();
						getOutput().getSurface().setPaused(true);
					}
				} else {
					if ( getOutput() != null && getOutput().getSurface() != null ) {
						getOutput().getSurface().setPaused(previousState);
					}
				}
			}
		};
		SwtGui.getWindow().addPerspectiveListener(perspectiveListener);
		
		return c;
	}
	
	public void createSurface(Composite c)
	{
		GLData gldata = new GLData();
        gldata.doubleBuffer = true;
		JOGLSWTDisplaySurface s = new JOGLSWTDisplaySurface(c,SWT.NO_BACKGROUND);
		s.initialize(((LayeredDisplayOutput) getOutput()).getEnvWidth(),((LayeredDisplayOutput) getOutput()).getEnvHeight(),getOutput());

		getOutput().setSurface(s);
	}
	
	
//    protected static void setup( GL gl2, int width, int height ) {
//        gl2.glMatrixMode( GL.GL_PROJECTION );
//        gl2.glLoadIdentity();
//
//        // coordinate system origin at lower left with width and height same as the window
//        GLU glu = new GLU();
//        glu.gluOrtho2D( 0.0f, width, 0.0f, height );
//
//        gl2.glMatrixMode( GL.GL_MODELVIEW );
//        gl2.glLoadIdentity();
//
//        gl2.glViewport( 0, 0, width, height );
//    }
//
//    protected static void render( GL gl2, int width, int height ) {
//        gl2.glClear( GL.GL_COLOR_BUFFER_BIT );
//        // draw a triangle filling the window
//        gl2.glLoadIdentity();
//        gl2.glBegin( GL.GL_TRIANGLES );
//        gl2.glColor3f( 1, 0, 0 );
//        gl2.glVertex2f( 0, 0 );
//        gl2.glColor3f( 0, 1, 0 );
//        gl2.glVertex2f( width, 0 );
//        gl2.glColor3f( 0, 0, 1 );
//        gl2.glVertex2f( width / 2, height );
//        gl2.glEnd();
//    }
	
}
