package com.yejin.ch4;

import com.yejin.ch4.image.S3ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class Ch4ApplicationTests {

	@MockitoBean
	private S3ImageService s3ImageService;

	@Test
	void contextLoads() {
	}

}
