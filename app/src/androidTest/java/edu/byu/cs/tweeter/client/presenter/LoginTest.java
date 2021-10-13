package edu.byu.cs.tweeter.client.presenter;

//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.stubbing.Answer;

//import edu.byu.cs.tweeter.client.cache.Cache;
//import edu.byu.cs.tweeter.client.model.service.UserService;
//import edu.byu.cs.tweeter.client.presenter.MainPresenter;
//import edu.byu.cs.tweeter.model.domain.User;


import org.junit.Test;

public class LoginTest {
    @Test
    public void testLogout_logoutSucceeds() {

    }

    @Test
    public void testLogout_logoutFails() {

    }
}



//    private MainPresenter.View mockMainView;
//    private UserService mockUserService;
//    private Cache mockCache;
//
//    private MainPresenter mainPresenterSpy;
//
//    @Before
//    public void setup() {
//        mockMainView = Mockito.mock(MainPresenter.View.class);
//        mockUserService = Mockito.mock(UserService.class);
//        mockCache = Mockito.mock(Cache.class);
//
//        User user = new User();
//        mainPresenterSpy = Mockito.spy(new MainPresenter(mockMainView/*, user, false*/));
//        Mockito.doReturn(mockUserService).when(mainPresenterSpy).getStatusService();
//
//        Cache.setInstance(mockCache);
//    }
//
//    @Test
//    public void testLogin_loginSucceeds() {
//        //Setup test case
//        Answer<Void> logoutSucceededAnswer = new Answer<Void>() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                UserService.LogoutObserver observer = invocation.getArgument(1);
//                observer.logoutSucceeded();
//                return null; //because logout doesn't have a return value
//            }
//        };
//
//        Mockito.doAnswer(logoutSucceededAnswer).when(mockUserService).logout(Mockito.any());
//
//        //Run test case
//        mainPresenterSpy.logout();
//
//        //Verify that mocks and spies were called correctly
//        Mockito.verify(mockMainView).displayInfoMessage("Logging Out...");
////        Mockito.verify(mockMainView).clearInfoMessage();
////        Mockito.verify(mockMainView).navigateToLogin();
//
//        //Verify that cache was cleared
//        Mockito.verify(mockCache).clearCache();
//    }
//}
