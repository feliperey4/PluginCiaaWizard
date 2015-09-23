package pluginciaa.wizards;

import java.io.InputStream;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.cdt.managedbuilder.ui.wizards.CfgHolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import pluginciaa.utils.UtilsCiaa;
import pluginciaa.utils.UtilsZip;

/**
 * 
 * @author Felipe Rey
 *
 */

@SuppressWarnings("restriction")
public class CiaaWizard extends Wizard implements INewWizard {
	
	private CiaaWizardPage page;
	
	private static final String RESOURCES_CIAA="resources/base.zip";

	public CiaaWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	public void addPages() {
		page = new CiaaWizardPage();
		addPage(page);
	}

	public boolean performFinish() {

		final String nombreProyecto = page.getNombreProyecto();
		final String board = page.getBoard();
		final boolean isGenerico= page.isGenerico();

		WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
			@SuppressWarnings("deprecation")
			protected void execute(IProgressMonitor progressMonitor) {
				try {
					// 1.0 Copia proyecto base.
					progressMonitor
							.beginTask("Creando proyecto CIAA '"
									+ nombreProyecto + "' - ",
									IProgressMonitor.UNKNOWN);

					progressMonitor.subTask("Cargando contenido CIAA...");
					InputStream in= this.getClass().getResourceAsStream(RESOURCES_CIAA);
					java.nio.file.Path path = ResourcesPlugin.getWorkspace()
							.getRoot().getLocation().toFile().toPath()
							.resolve(nombreProyecto);
					UtilsZip.unZip(in, path.toFile().getAbsolutePath());
					progressMonitor.worked(1);

					// 1.1 Cambia nombre de carpeta de proyecto.
					progressMonitor.subTask("Actualizando contenido CIAA...");
					UtilsCiaa.setNombreProyecto(nombreProyecto, path,isGenerico);

					// 2.0 Configura archivo .mime, Nombre de proyecto y board.
					UtilsCiaa.actualizarMime(path, board, nombreProyecto);
					progressMonitor.worked(1);

					// Crea proyecto en workspaces.
					progressMonitor
							.subTask("Cargando configuracion de proyecto C...");
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					IProject project = workspace.getRoot().getProject(
							nombreProyecto);

					IProjectDescription description = workspace
							.newProjectDescription(nombreProyecto);
					description.setLocation(project.getLocation());
				
					CCorePlugin.getDefault().createCDTProject(description,
							project, progressMonitor);

					ICProjectDescriptionManager pdMgr = CoreModel.getDefault()
							.getProjectDescriptionManager();
					ICProjectDescription projDesc = pdMgr
							.createProjectDescription(project, false);
					ManagedBuildInfo info = ManagedBuildManager
							.createBuildInfo(project);
					ManagedProject mProj = new ManagedProject(projDesc);
					info.setManagedProject(mProj);

					CfgHolder cfgHolder = new CfgHolder(null, null);
					String s = "0"; 
					Configuration config = new Configuration(mProj, null,
							ManagedBuildManager.calculateChildId(s, null),
							cfgHolder.getName());
					String id = config.getId();
					IBuilder builder = config.getEditableBuilder();
					builder.setManagedBuildOn(false);
					builder.setAutoBuildEnable(true);
					builder.setAutoBuildTarget("");
					builder.setIncrementalBuildTarget("");
										
					CConfigurationData data = config.getConfigurationData();
					projDesc.createConfiguration(
							ManagedBuildManager.CFG_DATA_PROVIDER_ID, data);

					pdMgr.setProjectDescription(project, projDesc);
					progressMonitor.worked(1);

					progressMonitor
							.subTask("Agregando variables de entorno CIAA...");
					UtilsCiaa.setVariablesEntorno(path, id,board);
					progressMonitor.worked(1);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					progressMonitor.done();
				}
			}
		};

		try {
			getContainer().run(false, false, operation);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}
}