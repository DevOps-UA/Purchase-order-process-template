/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.com.integrity.process.techorder.activity.servicetask;

import javax.annotation.Resource;

import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import ua.com.integrity.model.Letter;
import ua.com.integrity.service.LetterTemplateService;
import ua.com.integrity.service.MailService;

@Component
public class TechOrderReminder implements JavaDelegate {

    @Resource
    private MailService mailService;

    @Resource
    private LetterTemplateService letterTemplateService;

    public void execute(DelegateExecution delegateExecution) throws Exception {
        String processInstanceId = delegateExecution.getProcessInstanceId();
        ProcessEngineServices es = delegateExecution.getProcessEngineServices();
        TaskService ts = es.getTaskService();
        for (Task task : ts.createTaskQuery().processInstanceId(processInstanceId).active().list()) {
            Letter letter = letterTemplateService.generateReminderForTechOrder(processInstanceId, task.getId());
            String taskAssignee = task.getAssignee();
            mailService.sendMail(taskAssignee, letter.getSubject(), letter.getBody());
        }
    }

}
