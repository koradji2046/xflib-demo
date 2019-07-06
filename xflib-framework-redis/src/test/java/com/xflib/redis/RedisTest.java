package com.xflib.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.xflib.framework.redis.JsonRedisTemplate;
import com.xflib.framework.redis.jedis.utils.DynamicRedisHolder;

import junit.framework.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

//    @Resource
	@Autowired
	private JsonRedisTemplate redis;

//}
//
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = TestRunner.class)
//@EnableRedis
//public class RedisTest {

	// @Autowired
	// private WebApplicationContext wac;
	//
	// private MockMvc mockMvc;
	//
	// @Before
	// public void setupMockMvc(){
	// mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	// }

//	@Autowired
//	private JsonRedisTemplate redis;

	@Test
	public void AddOne() throws Exception {
		_run("default", "default");
		_run("30001", "xk");
		_run("30002", "xk");
		_run("30003", "default");
	}

	private void _run(String site, String source) {
		DynamicRedisHolder.setContext(site, source);

		redis.opsForValue().set("b", "abc");
		String b = (String) redis.opsForValue().get("b");
		Assert.assertSame("从redis没有获取到正确的值", b, "abc");

		redis.opsForValue().set("a", 1);
		int a = (int) redis.opsForValue().get("a");
		Assert.assertSame("从redis没有获取到正确的值", a, 1);

		DynamicRedisHolder.removeContext();
	}
}
