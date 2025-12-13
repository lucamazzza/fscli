package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.model.ApplicationModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AboutControllerTest {

    @Mock
    private ApplicationModel mockModel;

    @Test
    void testShowAppInfoDelegatesToModel() {
        AboutController controller = new AboutController();
        controller.setModel(mockModel);
        controller.showAppInfo();
        verify(mockModel).getAppInfo();
    }
}