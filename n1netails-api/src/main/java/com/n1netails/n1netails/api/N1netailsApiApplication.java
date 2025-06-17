package com.n1netails.n1netails.api;

import com.n1netails.n1netails.api.ai.llm.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class N1netailsApiApplication implements CommandLineRunner {

	private final OpenAiService openAiService;

	public final static String LARGE_JAVA_EXCEPTION_STACK_TRACE =
			"""
            java.lang.RuntimeException: Failed to retrieve user profile from database
            at com.n1netails.account.service.UserProfileService.getUserProfile(UserProfileService.java:94)
            at com.n1netails.account.controller.UserController.getProfile(UserController.java:52)
            at sun.reflect.NativeMethodAccessorImpl.invoke0(
            Native Method)
            at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
            at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
            at java.lang.reflect.Method.invoke(Method.java:566)
            at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:205)
            at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:133)
            at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:97)
            at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:891)
            at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:797)
            at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
            at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:991)
            at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:925)
            at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:974)
            at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:866)
            at javax.servlet.http.HttpServlet.service(HttpServlet.java:635)
            at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:851)
            at javax.servlet.http.HttpServlet.service(HttpServlet.java:742)
            at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)
            at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
            at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)
            at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
            at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
            at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:113)
            at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)
            at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
            at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
            at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
            at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:202)
            at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96)
            at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:541)
            at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139)
            at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
            at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)
            at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343)
            at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:373)
            at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65)
            at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:868)
            at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1590)
            at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
            at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
            at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
            at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
            at java.lang.Thread.run(Thread.java:829)
            Caused by: java.sql.SQLSyntaxErrorException: Unknown column 'user_emailz' in 'field list'
            at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:118)
            at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:95)
            at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:87)
            at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:61)
            at com.mysql.cj.jdbc.ClientPreparedStatement.executeInternal(ClientPreparedStatement.java:953)
            at com.mysql.cj.jdbc.ClientPreparedStatement.executeQuery(ClientPreparedStatement.java:1003)
            at org.springframework.jdbc.core.JdbcTemplate$1.doInPreparedStatement(JdbcTemplate.java:699)
            at org.springframework.jdbc.core.JdbcTemplate.execute(JdbcTemplate.java:639)
            at org.springframework.jdbc.core.JdbcTemplate.query(JdbcTemplate.java:712)
            at org.springframework.jdbc.core.JdbcTemplate.query(JdbcTemplate.java:737)
            at org.springframework.jdbc.core.JdbcTemplate.queryForObject(JdbcTemplate.java:805)
            at com.n1netails.account.repository.UserRepository.findByEmail(UserRepository.java:56)
            at com.n1netails.account.service.UserProfileService.getUserProfile(UserProfileService.java:88)
                    ... 45 more
            """;

	public static void main(String[] args) {
		SpringApplication.run(N1netailsApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		System.out.println("STARTING PROMPT");
		openAiService.completePrompt(LARGE_JAVA_EXCEPTION_STACK_TRACE);

	}
}
