import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;


public class Example {

	public static void main (String [] args) {
		final Display display = new Display ();
		Shell shell = new Shell (display);
		final ProgressBar bar = new ProgressBar (shell, SWT.SMOOTH);
		Rectangle clientArea = shell.getClientArea ();
		bar.setBounds (clientArea.x, clientArea.y, 200, 32);
		shell.open ();
		
		display.timerExec(100, new Runnable() {
			int i = 0;
			@Override
			public void run() {
				if (bar.isDisposed()) return;
				bar.setSelection(i++);
				if (i <= bar.getMaximum()) display.timerExec(100, this);
			}
		});

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}
