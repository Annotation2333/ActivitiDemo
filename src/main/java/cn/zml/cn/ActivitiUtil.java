package cn.zml.cn;

import groovy.util.logging.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author zml
 * @date 2023-02-01 15:11:49
 */
public class ActivitiUtil {

    /**
     *使用activiti提供的默认方式来创建mysql的表
     */
    @Test
    public void createTable(){
        //执行该方法，会在数据库自动生成activiti所需的25张表
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(engine);
    }

    /**
     * 发布（部署）请假流程
     */
    @Test
    public void  deploymentLeave(){
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = engine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("bpmn/Leave.bpmn")//添加bpmn资源)
                .name("请假申请流程")
                .deploy();
        //输出部署信息
        System.out.println("流程部署Id：" + deploy.getId());
        System.out.println("流程部署名称：" + deploy.getName());
    }

    /**
     * 启动流程实例,相当于开启一个流程
     */
    @Test
    public void testStartProcess(){
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        //通过runTimeService启动流程，获取runTimeService
        RuntimeService runtimeService = engine.getRuntimeService();
        //启动流程，这个 key是当时定义Leave.bpmn时填写的id(即第一张bpmn图片，左侧蓝色框框中，id对应的值，这个是自定义的)
        ProcessInstance myLeave = runtimeService.startProcessInstanceByKey("myLeave");
        //打印信息
        System.out.println(myLeave.getProcessDefinitionId());//流程定义id
        System.out.println(myLeave.getId());//流程实例id
        System.out.println(myLeave.getActivityId());//当前活动id

    }


    /**
     * 查询任务负责人为work的任务列表
     */
    @Test
    public void taskList(){
        String assigner = "work";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        List<Task> taskList = taskService.createTaskQuery()
                .processInstanceId("2501")//流程实例id
                .taskAssignee(assigner)
                .list();
       for (Task task :taskList){
           System.out.println("流程实例id: "+task.getProcessInstanceId());
           System.out.println("任务id: "+task.getId());
           System.out.println("任务名称: "+task.getName());
           System.out.println("任务负责人: "+task.getAssignee());
       }
    }

    /**
     * 任务负责人work执行任务，执行完毕后，进入流程下一节点（manager）
     */
    @Test
    public void completeTask(){
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId("2501").taskAssignee("work").singleResult();
        taskService.complete(task.getId());
    }

}
