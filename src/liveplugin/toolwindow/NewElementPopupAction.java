/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package liveplugin.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeBundle;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileChooser.actions.NewFolderAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.IconLoader;
import liveplugin.IdeUtil;
import liveplugin.toolwindow.addplugin.AddNewPluginAction;
import liveplugin.toolwindow.addplugin.AddPluginFromDiskAction;
import liveplugin.toolwindow.addplugin.git.AddPluginFromGistAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static liveplugin.LivePluginAppComponent.*;
import static liveplugin.pluginrunner.GroovyPluginRunner.MAIN_SCRIPT;
import static liveplugin.pluginrunner.GroovyPluginRunner.TEST_SCRIPT;

class NewElementPopupAction extends AnAction implements DumbAware, PopupAction {
	public static final Icon Folder = IconLoader.getIcon("/nodes/folder.png"); // 16x16

	@Override public void actionPerformed(final AnActionEvent event) {
		showPopup(event.getDataContext());
	}

	private void showPopup(DataContext context) {
		createPopup(context).showInBestPositionFor(context);
	}

	private ListPopup createPopup(DataContext dataContext) {
		return JBPopupFactory.getInstance().createActionGroupPopup(
				IdeBundle.message("title.popup.new.element"),
				createActionGroup(),
				dataContext, false, true, false, null, -1,
				LangDataKeys.PRESELECT_NEW_ACTION_CONDITION.getData(dataContext)
		);
	}

	private ActionGroup createActionGroup() {
		return new ActionGroup() {
			@NotNull @Override
			public AnAction[] getChildren(AnActionEvent e) {
				Icon iconByFileType = null;

				List<AnAction> actions = new ArrayList<AnAction>();
				actions.add(new NewFileAction("Groovy File", iconByFileType, IdeUtil.GROOVY_FILE_TYPE));

				if (scalaIsOnClassPath()) actions.add(new NewFileAction("Scala File", iconByFileType, IdeUtil.SCALA_FILE_TYPE));
				if (clojureIsOnClassPath()) actions.add(new NewFileAction("Clojure File", iconByFileType, IdeUtil.CLOJURE_FILE_TYPE));

				actions.addAll(asList(
						new NewFileAction("Text File", AllIcons.FileTypes.Text, IdeUtil.TEXT_FILE_TYPE),
						new NewFolderAction("Directory", "Directory", Folder)
				));
				actions.addAll(asList(
						new CreateRootFileAction(MAIN_SCRIPT, MAIN_SCRIPT, defaultPluginScript(), iconByFileType, IdeUtil.GROOVY_FILE_TYPE),
						new CreateRootFileAction(TEST_SCRIPT, TEST_SCRIPT, defaultPluginTestScript(), iconByFileType, IdeUtil.GROOVY_FILE_TYPE)
				));
				actions.addAll(asList(
						new Separator(),
						new AddNewPluginAction(),
						new AddPluginFromDiskAction()
				));
				actions.add(new AddPluginFromGistAction());
				if (PluginToolWindowManager.addFromGitHubAction != null) {
					actions.add(PluginToolWindowManager.addFromGitHubAction);
				}
				return actions.toArray(new AnAction[actions.size()]);
			}
		};
	}

	@Override public void update(AnActionEvent e) {
		Presentation presentation = e.getPresentation();
		presentation.setEnabled(true);
	}
}
