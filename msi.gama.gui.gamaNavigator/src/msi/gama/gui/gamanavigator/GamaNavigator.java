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
package msi.gama.gui.gamanavigator;

import org.eclipse.ui.navigator.CommonNavigator;

public class GamaNavigator extends CommonNavigator {

	String OPEN_BROWSER_COMMAND_ID = "msi.gama.gui.application.commands.OpenBrowser";

	// @Override
	// public CommonViewer getCommonViewer() {
	// return super.getCommonViewer();
	// }

	@Override
	protected Object getInitialInput() {
		return new NavigatorRoot();
	}

	// @Override
	// protected void handleDoubleClick(DoubleClickEvent anEvent) {
	// ICommandService commandService = (ICommandService) getSite()
	// .getService(ICommandService.class);
	// Command command = commandService.getCommand(OPEN_BROWSER_COMMAND_ID);
	//
	// IWorkbenchWindow window = PlatformUI.getWorkbench()
	// .getActiveWorkbenchWindow();
	// ISelectionService selectionService = window.getSelectionService();
	// ISelection iselection = selectionService.getSelection();
	//
	// Object selection = ((IStructuredSelection) iselection)
	// .getFirstElement();
	//
	// if (selection instanceof IResource) {
	// super.handleDoubleClick(anEvent);
	// return;
	// } else {
	// if (command.isEnabled()) {
	// IHandlerService handlerService = (IHandlerService) getSite()
	// .getService(IHandlerService.class);
	// try {
	// handlerService
	// .executeCommand(OPEN_BROWSER_COMMAND_ID, null);
	// } catch (NotDefinedException e) {
	// throw new RuntimeException("Could not find open command: "
	// + OPEN_BROWSER_COMMAND_ID);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// } else {
	// super.handleDoubleClick(anEvent);
	// }
	// }
	// }

}
