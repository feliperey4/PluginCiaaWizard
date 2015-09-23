package pluginciaa.wizards;

import java.nio.file.Path;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import pluginciaa.utils.UtilsCiaa;

/**
 * 
 * @author Felipe Rey
 *
 */

public class CiaaWizardPage extends WizardPage {
	
	private Text nombreProyecto;
	
	private Combo board;
	
	private Group group1;
	
	private Button botonGenerico;
	
	public CiaaWizardPage() {
		super("wizardPage");
		setTitle("CIAA-Project");
		setDescription("This wizard creates a new project CIAA.");		
	}
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 20;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Nombre del projecto:");
		
		// TEXT
		nombreProyecto = new Text(container, SWT.BORDER | SWT.SINGLE );
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		nombreProyecto.setLayoutData(gd);
		nombreProyecto.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		// Combo box
		label = new Label(container, SWT.NULL);
		label.setText("&Dispositivo:");
		
		board=new Combo(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		board.setLayoutData(gd);
		
		// Radio button
		label = new Label(container, SWT.NULL);
		label.setText(" ");
		
		group1 = new Group(container, SWT.SHADOW_IN);
	    group1.setText("Tipo de proyecto:");
	    RowLayout row=new RowLayout(SWT.HORIZONTAL);
	    row.marginTop=10;
	    row.marginRight=20;
	    row.marginLeft=20;
	    row.marginBottom=10;
	    row.spacing=15;
	    group1.setLayout(row);
	    botonGenerico=new Button(group1, SWT.RADIO);
	    botonGenerico.setText("Generico");
	    new Button(group1, SWT.RADIO).setText("BareMetal");
	    
	    gd = new GridData(GridData.FILL_HORIZONTAL);
	    group1.setLayoutData(gd);

		initialize();
		dialogChanged();
		setControl(container);
	}

	private void initialize() {
		board.setItems(UtilsCiaa.LISTA_BOARD);
		board.select(2);
		botonGenerico.setSelection(true);
	}

	private void dialogChanged() {		
		Path  path= ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile().toPath().resolve(getNombreProyecto());
		if (getNombreProyecto().length() == 0) {
			updateStatus("Especifique el nombre del nuevo proyecto");
			return;
		}
		if (path==null || path.toFile().exists()) {
			updateStatus("El proyecto ya existe");
			return;
		}

		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getNombreProyecto() {
		return nombreProyecto.getText();
	}
	
	public String getBoard(){
		return board.getItem(board.getSelectionIndex());
	}

	public boolean isGenerico(){
		return botonGenerico.getSelection();
	}
}