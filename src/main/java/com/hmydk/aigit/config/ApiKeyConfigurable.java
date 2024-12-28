package com.hmydk.aigit.config;

import com.hmydk.aigit.constant.Constants;
import com.hmydk.aigit.pojo.PromptInfo;
import com.hmydk.aigit.util.ProjectUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ApiKeyConfigurable implements Configurable {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyConfigurable.class);
    private ApiKeyConfigurableUI ui;
    private final ApiKeySettings settings = ApiKeySettings.getInstance();
    private Project currentProject;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "AI Git Commit";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        ui = new ApiKeyConfigurableUI();
        currentProject = ProjectUtil.getCurrentProject();
        loadSettings();
        return ui.getMainPanel();
    }

    @Override
    public boolean isModified() {
        return !settings.getProjectSettings(currentProject).getSelectedClient().equals(ui.getClientComboBox().getSelectedItem())
                || !settings.getProjectSettings(currentProject).getSelectedModule().equals(ui.getModuleComboBox().getSelectedItem())
                || !settings.getProjectSettings(currentProject).getCommitLanguage().equals(ui.getLanguageComboBox().getSelectedItem())
                || isCustomPromptsModified() || isCustomPromptModified() || isPromptTypeModified();
    }

    @Override
    public void apply() {
        settings.getProjectSettings(currentProject).setSelectedClient((String) ui.getClientComboBox().getSelectedItem());
        settings.getProjectSettings(currentProject).setSelectedModule((String) ui.getModuleComboBox().getSelectedItem());
        settings.getProjectSettings(currentProject).setCommitLanguage((String) ui.getLanguageComboBox().getSelectedItem());

        // Save prompt content
        Object selectedPromptType = ui.getPromptTypeComboBox().getSelectedItem();
        if (Constants.CUSTOM_PROMPT.equals((String) selectedPromptType)) {
            saveCustomPromptsAndChoosedPrompt();
        }
        // Save prompt type
        settings.getProjectSettings(currentProject).setPromptType((String) selectedPromptType);
    }

    @Override
    public void reset() {
        loadSettings();
    }

    @Override
    public void disposeUIResources() {
        ui = null;
    }

    private void loadSettings() {
        if (ui != null) {
            ui.getClientComboBox().setSelectedItem(settings.getProjectSettings(currentProject).getSelectedClient());
            ui.getModuleComboBox().setSelectedItem(settings.getProjectSettings(currentProject).getSelectedModule());
            ui.getLanguageComboBox().setSelectedItem(settings.getProjectSettings(currentProject).getCommitLanguage());

            // Set table data
            loadCustomPrompts();
            // Set selected item in combo box
            loadChoosedPrompt();

            // Set prompt type
            ui.getPromptTypeComboBox().setSelectedItem(settings.getProjectSettings(currentProject).getPromptType());
        }
    }

    private void loadCustomPrompts() {
        DefaultTableModel model = (DefaultTableModel) ui.getCustomPromptsTable().getModel();
        model.setRowCount(0);
        for (PromptInfo prompt : settings.getProjectSettings(currentProject).getCustomPrompts()) {
            if (prompt != null) {
                model.addRow(new String[]{prompt.getDescription(), prompt.getPrompt()});
            }
        }
    }

    private void loadChoosedPrompt() {
        if (settings.getProjectSettings(currentProject).getCustomPrompt() != null) {
            DefaultTableModel model = (DefaultTableModel) ui.getCustomPromptsTable().getModel();
            int rowCount = model.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String description = (String) model.getValueAt(i, 0);
                String prompt = (String) model.getValueAt(i, 1);
                if (settings.getProjectSettings(currentProject).getCustomPrompt().getDescription().equals(description)
                        && settings.getProjectSettings(currentProject).getCustomPrompt().getPrompt().equals(prompt)) {
                    ui.getCustomPromptsTable().setRowSelectionInterval(i, i);
                }
            }
        }
    }

    private void saveCustomPromptsAndChoosedPrompt() {
        DefaultTableModel model = (DefaultTableModel) ui.getCustomPromptsTable().getModel();
        int selectedRow = ui.getSELECTED_ROW();
        int rowCount = model.getRowCount();
        List<PromptInfo> customPrompts = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            String description = (String) model.getValueAt(i, 0);
            String prompt = (String) model.getValueAt(i, 1);
            PromptInfo promptInfo = new PromptInfo(description, prompt);
            customPrompts.add(i, promptInfo);

            // Handle selected row data as new prompt
            if (selectedRow == i) {
                settings.getProjectSettings(currentProject).setCustomPrompt(promptInfo);
            }
        }
        settings.getProjectSettings(currentProject).setCustomPrompts(customPrompts);
    }

    private boolean isPromptTypeModified() {
        Object selectedPromptType = ui.getPromptTypeComboBox().getSelectedItem();
        return !settings.getProjectSettings(currentProject).getPromptType().equals(selectedPromptType);
    }

    private boolean isCustomPromptsModified() {
        DefaultTableModel model = (DefaultTableModel) ui.getCustomPromptsTable().getModel();
        int rowCount = model.getRowCount();
        if (rowCount != settings.getProjectSettings(currentProject).getCustomPrompts().size()) {
            return true;
        }
        for (int i = 0; i < rowCount; i++) {
            if (!model.getValueAt(i, 0).equals(settings.getProjectSettings(currentProject).getCustomPrompts().get(i).getDescription())
                    || !model.getValueAt(i, 1).equals(settings.getProjectSettings(currentProject).getCustomPrompts().get(i).getDescription())) {
                return true;
            }
        }
        return false;
    }

    private boolean isCustomPromptModified() {
        int selectedRow = ui.getSELECTED_ROW();
        DefaultTableModel model = (DefaultTableModel) ui.getCustomPromptsTable().getModel();
        int tableRowCount = model.getRowCount();

        if (selectedRow >= tableRowCount) {
            return true;
        }

        return !model.getValueAt(selectedRow, 0).equals(settings.getProjectSettings(currentProject).getCustomPrompt().getDescription())
                || !model.getValueAt(selectedRow, 1).equals(settings.getProjectSettings(currentProject).getCustomPrompt().getDescription());
    }
}
