/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.svn;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;

/*
 * This class is an implementation of ISVNEventHandler intended for  processing   
 * events generated by do*() methods of an SVNWCClient object. An  instance  of 
 * this handler will be provided to an SVNWCClient. When  calling, for example, 
 * SVNWCClient.doDelete(..) on some path, that method will  generate  an  event 
 * for each 'delete' action it will perform upon every path being deleted.  And
 * this event is passed to 
 * 
 * ISVNEventHandler.handleEvent(SVNEvent event,  double progress) 
 * 
 * to notify the handler.  The  event  contains detailed  information about the 
 * path, action performed upon the path and some other. 
 */
public class WCEventHandler implements ISVNEventHandler {
    /*
     * progress  is  currently  reserved  for future purposes and now is always
     * ISVNEventHandler.UNKNOWN  
     */
    @Override
	public void handleEvent(SVNEvent event, double progress) {
        /*
         * Gets the current action. An action is represented by SVNEventAction.
         */
        SVNEventAction action = event.getAction();
        if (action == SVNEventAction.ADD){
            /*
             * The item is scheduled for addition.
             */
            System.out.println("A     " + event.getURL());
            return;
        }else if (action == SVNEventAction.COPY){
            /*
             * The  item  is  scheduled for addition  with history (copied,  in 
             * other words).
             */
            System.out.println("A  +  " + event.getURL());
            return;
        }else if (action == SVNEventAction.DELETE){
            /*
             * The item is scheduled for deletion. 
             */
            System.out.println("D     " + event.getURL());
            return;
        } else if (action == SVNEventAction.LOCKED){
            /*
             * The item is locked.
             */
            System.out.println("L     " + event.getURL());
            return;
        } else if (action == SVNEventAction.LOCK_FAILED){
            /*
             * Locking operation failed.
             */
            System.out.println("failed to lock    " + event.getURL());
            return;
        }
    }

    /*
     * Should be implemented to check if the current operation is cancelled. If 
     * it is, this method should throw an SVNCancelException. 
     */
    @Override
	public void checkCancelled() throws SVNCancelException {
    }

}
