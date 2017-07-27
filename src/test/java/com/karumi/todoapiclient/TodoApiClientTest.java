/*
 *   Copyright (C) 2016 Karumi.
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.karumi.todoapiclient;

import com.karumi.todoapiclient.dto.TaskDto;
import com.karumi.todoapiclient.exception.ItemNotFoundException;
import com.karumi.todoapiclient.exception.NetworkErrorException;
import com.karumi.todoapiclient.exception.TodoApiClientException;
import com.karumi.todoapiclient.exception.UnknownErrorException;

import java.io.IOException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TodoApiClientTest extends MockWebServerTest {

  private TodoApiClient apiClient;

  @Before public void setUp() throws Exception {
    super.setUp();
    String mockWebServerEndpoint = getBaseEndpoint();
    apiClient = new TodoApiClient(mockWebServerEndpoint);
  }

  //GET ALL TASKS

  @Test
  public void shouldReturnTheTasksFromToDoEndpoint () throws Exception {
    enqueueMockResponse(200,"getTasksResponse.json");

    List<TaskDto> tasks = apiClient.getAllTasks();

    assertEquals(200, tasks.size());
  }

  @Test
  public void shouldReturnTheValuesFromFirstTask () throws Exception {
    enqueueMockResponse(200, "getTasksResponse.json");

    List<TaskDto> tasks = apiClient.getAllTasks();

    assertTaskContainsExpectedValues(tasks.get(0));
  }

  @Test
  public void shouldGetCallToTheCorrectPath () throws Exception {
    enqueueMockResponse();

    apiClient.getAllTasks();

    assertGetRequestSentTo("/todos");
  }

  @Test (expected = UnknownErrorException.class)
  public void shouldLaunchException () throws Exception {
    enqueueMockResponse(418);

    apiClient.getAllTasks();

  }

  //GET ONE TASK

  @Test
  public void shouldGetOneTaskCorrectResponseAndPath () throws Exception{
    enqueueMockResponse();

    apiClient.getTaskById("100");

    assertGetRequestSentTo("/todos/100");
  }


  @Test (expected = ItemNotFoundException.class)
  public void shouldGetOneTaskServerReturns404 () throws Exception
  {
    enqueueMockResponse(404);

    apiClient.getTaskById("1000");

  }

  @Test
  public void shouldGetOneTaskCorrectBody () throws Exception {
    enqueueMockResponse(200,"getTaskByIdResponse.json");

    TaskDto task = apiClient.getTaskById("1");

    assertTaskContainsExpectedValues(task);
  }

  @Test (expected = UnknownErrorException.class)
  public void shouldGetOneTaskServerReturns500 () throws Exception {
    enqueueMockResponse(500);

    apiClient.getTaskById("1000");

  }

  //ADD TASK

  @Test
  public void shouldAddOneTaskCorrectPath () throws Exception {
    enqueueMockResponse();

    TaskDto task = new TaskDto("201","1","Hi Puri",false);
    apiClient.addTask(task);

    assertPostRequestSentTo("/todos");
  }

  @Test
  public void shouldAddOneTaskCorrectBody () throws Exception {
    enqueueMockResponse(201,"addTaskResponse.json");

    TaskDto task = new TaskDto("1","1","delectus aut autem",false);
    TaskDto addedTask = apiClient.addTask(task);

    assertTaskContainsExpectedValues(addedTask);
  }

  @Test
  public void shouldAddOneTaskSendCorrectBody () throws Exception {
    enqueueMockResponse();

    TaskDto task = new TaskDto("1","2","Finish this kata",false);
    apiClient.addTask(task);

    assertRequestBodyEquals("addTaskRequest.json");
  }

  @Test (expected = UnknownErrorException.class)
  public void shouldAddOneTaskServerReturns418 () throws Exception {
    enqueueMockResponse(418);

    TaskDto task = new TaskDto("1","2","Finish this kata",false);
    apiClient.addTask(task);
  }


  @Test (expected = UnknownErrorException.class)
  public void shouldAddOneTaskServerReturns500 () throws Exception {
    enqueueMockResponse(500);

    TaskDto task = new TaskDto("1","2","Finish this kata",false);
    apiClient.addTask(task);
  }

  //UPDATE ONE TASK
  @Test
  public void shouldUpdateOneTaskCorrectPath () throws Exception {
    enqueueMockResponse();

    TaskDto task = new TaskDto("1","1","Hi Puri",false);
    apiClient.updateTaskById(task);

    assertPutRequestSentTo("/todos/1");
  }

    @Test
    public void shouldUpdateOneTaskCorrectBody () throws Exception {
        enqueueMockResponse(201,"updateTaskResponse.json");

        TaskDto task = new TaskDto("1","1","delectus aut autem",false);
        TaskDto updatedTask = apiClient.updateTaskById(task);

        assertTaskContainsExpectedValues(updatedTask);
    }

    @Test
    public void shouldUpdateOneTaskSendCorrectBody () throws Exception {
        enqueueMockResponse();

        TaskDto task = new TaskDto("1","2","Finish this kata",false);
        apiClient.updateTaskById(task);

        assertRequestBodyEquals("updateTaskRequest.json");
    }

    @Test (expected = UnknownErrorException.class)
    public void shouldUpdateOneTaskServerReturns418 () throws Exception {
        enqueueMockResponse(418);

        TaskDto task = new TaskDto("1","2","Finish this kata",false);
        apiClient.updateTaskById(task);
    }


    @Test (expected = UnknownErrorException.class)
    public void shouldUpdateOneTaskServerReturns500 () throws Exception {
        enqueueMockResponse(500);

        TaskDto task = new TaskDto("1","2","Finish this kata",false);
        apiClient.updateTaskById(task);
    }


  private void assertTaskContainsExpectedValues(TaskDto task) {
    assertEquals(task.getId(), "1");
    assertEquals(task.getUserId(), "1");
    assertEquals(task.getTitle(), "delectus aut autem");
    assertFalse(task.isFinished());
  }
}
