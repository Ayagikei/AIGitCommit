package com.hmydk.aigit.util;

import com.hmydk.aigit.config.ApiKeySettings;
import com.hmydk.aigit.constant.Constants;
import com.intellij.openapi.project.Project;

/**
 * PromptUtil
 *
 * @author hmydk
 */
public class PromptUtil {

    public static final String DEFAULT_PROMPT_0 = getDefaultPrompt0();
    public static final String DEFAULT_PROMPT_1 = getDefaultPrompt();
    public static final String DEFAULT_PROMPT_2 = getPrompt3();
    public static final String DEFAULT_PROMPT_3 = getPrompt4();

    public static String constructPrompt(String diff, Project currentProject) {
        String promptContent = "";

        // get prompt content
        ApiKeySettings settings = ApiKeySettings.getInstance();
        if (Constants.PROJECT_PROMPT.equals(settings.getProjectSettings(currentProject).getPromptType())) {
            promptContent = FileUtil.loadProjectPrompt(currentProject);
        } else {
            promptContent = settings.getProjectSettings(currentProject).getCustomPrompt().getPrompt();
        }

        // check prompt content
        if (!promptContent.contains("{diff}")) {
            throw new IllegalArgumentException("The prompt file must contain the placeholder {diff}.");
        }
//        if (!promptContent.contains("{language}")) {
//            throw new IllegalArgumentException("The prompt file must contain the placeholder {language}.");
//        }

        // replace placeholder
        promptContent = promptContent.replace("{diff}", diff);
        promptContent = promptContent.replace("{language}", settings.getProjectSettings(currentProject).getCommitLanguage());
        return promptContent;
    }

    private static String getPrompt4() {
        return """
You are a Git commit message generation expert. Please analyze the following code changes and generate a clear, standardized commit message in {language}.

Code changes:
{diff}

Requirements for the commit message:
1. First line should start with one of these types:
   feat: (new feature)
   fix: (bug fix)
   docs: (documentation)
   style: (formatting)
   refactor: (code refactoring)
   perf: (performance)
   test: (testing)
   chore: (maintenance)

2. First line should be no longer than 72 characters

3. After the first line, leave one blank line and provide detailed explanation if needed:
   - Why was this change necessary?
   - How does it address the issue?
   - Any breaking changes?

4. Use present tense

Please output only the commit message, without any additional explanations.
                """;
    }

    private static String getDefaultPrompt0() {
        return """
                Generate a conventional Git commit message based on the provided code changes.
                
                Input:
                Code changes: {diff}
                
                Format requirements:
                [type][(optional)scope]: [gitmoji] [description]
                
                Possible types:
                - feat: New feature development
                - fix: Bug fixes
                - docs: Documentation changes
                - style: Code formatting (no logic changes)
                - refactor: Code refactoring
                - test: Test-related
                - chore: Build/toolchain related
                - perf: Performance optimization
                - ci: CI configuration changes
                - revert: Code rollback
                
                Possible scopes examples:
                - api: API related changes
                - ui: UI related changes
                - db: Database related changes
                
                Gitmoji guide:
                Core emojis:
                - ✨ :sparkles: New features
                - 🐛 :bug: Fix bugs
                - 📝 :memo: Documentation updates
                - 💄 :lipstick: UI/style updates
                - ♻️ :recycle: Code refactoring
                - ✅ :white_check_mark: Add tests
                - 🔧 :wrench: Modify configuration files
                
                Extended emojis:
                - ⚡️ :zap: Performance improvements
                - 🔥 :fire: Remove code/files
                - 🚑️ :ambulance: Critical hotfix
                - 🔒️ :lock: Fix security issues
                - 🚧 :construction: Work in progress
                - ⬆️ :arrow_up: Upgrade dependencies
                - ⬇️ :arrow_down: Downgrade dependencies
                - 🌐 :globe_with_meridians: Internationalization
                - 🚚 :truck: Move/rename files
                - 🏗️ :building_construction: Architectural changes
                - 🔍️ :mag: Improve SEO
                
                Description requirements:
                1. Use {language} for description
                2. Must start with a verb
                3. Keep length within 20 characters
                4. No punctuation at the end
                5. Description must specifically reflect code changes
                
                Examples:
                ✅ Good examples:
                feat(api): ✨ Add user auth API
                fix(ui): 🐛 Fix button click bug
                docs: 📝 Update deploy docs
                style: 🎨 Unify code indent
                refactor: ♻️ Refactor login flow
                test: ✅ Add unit tests
                chore: 🔧 Upgrade deps version
                
                ❌ Examples to avoid:
                feat: New feature (too vague)
                fix: Fix bug (not specific)
                feat: ✨ Made some updates (not specific)
                style: 🎨 Changed format. (has punctuation)
                
                Note: The whole result should be given in {language} and the final result must NOT contain '```', Possible types and Possible scopes do not need to be translated into {language}.
                """;
    }

    private static String getDefaultPrompt() {
        return """
                You are an AI assistant tasked with generating a Git commit message based on the provided code changes. Your goal is to create a clear, concise, and informative commit message that follows best practices.

                Input:
                - Code diff:
                ```
                {diff}
                ```

                Instructions:
                1. Analyze the provided code diff and branch name.
                2. Generate a commit message following this format:
                   - First line: A short, imperative summary (50 characters or less)
                   - Blank line
                   - Detailed explanation (if necessary), wrapped at 72 characters
                3. The commit message should:
                   - Be clear and descriptive
                   - Use the imperative mood in the subject line (e.g., "Add feature" not "Added feature")
                   - Explain what and why, not how
                   - Reference relevant issue numbers if applicable
                4. Avoid:
                   - Generic messages like "Bug fix" or "Update file.txt"
                   - Mentioning obvious details that can be seen in the diff

                Output:
                - Provide only the commit message, without any additional explanation or commentary.

                Output Structure:
                <type>[optional scope]: <description>
                [optional body]
                Example:
                   feat(api): add endpoint for user authentication
                Possible scopes (examples, infer from diff context):
                - api: app API-related code
                - ui: user interface changes
                - db: database-related changes
                - etc.
                Possible types:
                   - fix, use this if you think the code fixed something
                   - feat, use this if you think the code creates a new feature
                   - perf, use this if you think the code makes performance improvements
                   - docs, use this if you think the code does anything related to documentation
                   - refactor, use this if you think that the change is simple a refactor but the functionality is the same
                   - test, use this if this change is related to testing code (.spec, .test, etc)
                   - chore, use this for code related to maintenance tasks, build processes, or other non-user-facing changes. It typically includes tasks that don't directly impact the functionality but are necessary for the project's development and maintenance.
                   - ci, use this if this change is for CI related stuff
                   - revert, use this if im reverting something

                Note: The whole result should be given in {language} and the final result must not contain ‘```’
                """;
    }

    private static String getPrompt3() {
        return """
                 Generate a concise yet detailed git commit message using the following format and information:

                 <type>(<scope>): <subject>

                 <body>

                 <footer>

                 Use the following placeholders in your analysis:
                 - diff begin ：
                 {diff}
                 - diff end.

                 Guidelines:

                 1. <type>: Commit type (required)
                    - Use standard types: feat, fix, docs, style, refactor, perf, test, chore

                 2. <scope>: Area of impact (required)
                    - Briefly mention the specific component or module affected

                 3. <subject>: Short description (required)
                    - Summarize the main change in one sentence (max 50 characters)
                    - Use the imperative mood, e.g., "add" not "added" or "adds"
                    - Don't capitalize the first letter
                    - No period at the end

                 4. <body>: Detailed description (required)
                    - Explain the motivation for the change
                    - Describe the key modifications (max 3 bullet points)
                    - Mention any important technical details
                    - Use the imperative mood

                 5. <footer>: (optional)
                    - Note any breaking changes
                    - Reference related issues or PRs

                 Example:

                 feat(user-auth): implement two-factor authentication

                 • Add QR code generation for 2FA setup
                 • Integrate Google Authenticator API
                 • Update user settings for 2FA options

                 Notes:
                 - Keep the entire message under 300 characters
                 - Focus on what and why, not how
                 - Summarize diff to highlight key changes; don't include raw diff output

                Note: The whole result should be given in {language} and the final result must not contain ‘```’
                """;
    }
}
