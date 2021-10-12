package edu.byu.cs.tweeter.client.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.text.ParseException;

import edu.byu.cs.tweeter.client.model.service.StatusService;

public class PostStatusTest {
    private MainPresenter.View mockMainView;
    private StatusService mockStatusService;

    private MainPresenter mainPresenterSpy;

    @Before
    public void setup() {
        mockMainView = Mockito.mock(MainPresenter.View.class);
        mockStatusService = Mockito.mock(StatusService.class);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockMainView));
        Mockito.doReturn(mockStatusService).when(mainPresenterSpy).getStatusService();
    }

    @Test
    public void testPostStatusSuccess() throws ParseException {
        //Setup test case
        Answer<Void> logoutSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(1);
                observer.postStatusSucceeded("Successfully Posted!");
                return null; //because post status doesn't have a return value
            }
        };

        Mockito.doAnswer(logoutSucceededAnswer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any());

        //Run test case
        mainPresenterSpy.postStatus("This is a test post");

        //Verify that mocks and spies were called correctly
        Mockito.verify(mockMainView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockMainView).displayInfoMessage("Successfully Posted!");
    }

    @Test
    public void testPostStatusFailed() throws ParseException {
        //Setup test case
        Answer<Void> logoutSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(1);
                observer.handleFailed("Post status failed");
                return null; //because post status doesn't have a return value
            }
        };

        Mockito.doAnswer(logoutSucceededAnswer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any());

        //Run test case
        mainPresenterSpy.postStatus("This is a test post");

        //Verify that mocks and spies were called correctly
        Mockito.verify(mockMainView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockMainView).displayInfoMessage("Failed: Post status failed");
    }

    @Test
    public void testPostStatusThrewException() throws ParseException {
        //Setup test case
        Exception ex = new Exception();
        Answer<Void> logoutSucceededAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(1);
                observer.handleException(ex);
                return null; //because post status doesn't have a return value
            }
        };

        Mockito.doAnswer(logoutSucceededAnswer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any());

        //Run test case
        mainPresenterSpy.postStatus("This is a test post");

        //Verify that mocks and spies were called correctly
        Mockito.verify(mockMainView).displayInfoMessage("Posting Status...");
        Mockito.verify(mockMainView).displayInfoMessage("Failed because of exception: " + ex.getMessage());
    }
}
