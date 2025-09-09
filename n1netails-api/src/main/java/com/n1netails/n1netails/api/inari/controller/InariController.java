package com.n1netails.n1netails.api.inari.controller;

import com.n1netails.n1netails.api.inari.service.GitHubService;
import com.n1netails.n1netails.api.inari.service.InariService;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.service.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Inari Controller", description = "Operations related to Inari AI Proxy")
//@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/api/inari"}, produces = APPLICATION_JSON)
public class InariController {

    private final InariService inariService;
    private final GitHubService gitHubService;
    private final AuthorizationService authorizationService;

    @GetMapping("/github/installation/callback")
    public ResponseEntity<String> handleGitHubInstallationCallback(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @RequestParam("installation_id") String installationId,
            @RequestParam("setup_action") String setupAction) throws Exception {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        Optional<OrganizationEntity> organization = currentUser.getOrganizations().stream().findFirst();
        if (organization.isEmpty()) {
            return ResponseEntity.badRequest().body("User is not part of any organization.");
        }
        gitHubService.saveInstallationId(installationId, organization.get().getId());
        return ResponseEntity.ok("GitHub App installation successful.");
    }

    @Operation(summary = "Get all user repositories", responses = {
            @ApiResponse(responseCode = "200", description = "List result containing user repositories",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))
    })
    @GetMapping("/list-repositories")
    public ResponseEntity<List<String>> getUserRepositories(@RequestHeader(AUTHORIZATION) String authorizationHeader) throws Exception {
        gitHubService.checkAppAuth();
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        Optional<OrganizationEntity> organization = currentUser.getOrganizations().stream().findFirst();
        if (organization.isEmpty()) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
        return ResponseEntity.ok(gitHubService.listRepositories(organization.get().getId()));
    }

    @Operation(summary = "Get all user repository branches", responses = {
            @ApiResponse(responseCode = "200", description = "List result containing user repository branches",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))
    })
    @PostMapping("/list-repository-branches")
    public ResponseEntity<List<String>> getUserRepositoryBranches(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            // todo implement request arguments
            // owner
            // repository
    ) throws Exception {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        Optional<OrganizationEntity> organization = currentUser.getOrganizations().stream().findFirst();
        if (organization.isEmpty()) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
        return ResponseEntity.ok(gitHubService.listBranches(organization.get().getId(), "shahidfoy", "s3-demo-n1netails")); // FIXME: get owner and repo from request
    }

    @Operation(summary = "Create pull request from tail response and notes", responses = {
            @ApiResponse(responseCode = "200", description = "Pull request initiated",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/initiate-pull-request")
    public ResponseEntity<String> initiatePullRequest(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            // todo implement request arguments
            // tail response
            // owner
            // repository
            // branch
    ) throws Exception {

        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        Optional<OrganizationEntity> organization = currentUser.getOrganizations().stream().findFirst();
        if (organization.isEmpty()) {
            return ResponseEntity.badRequest().body("User is not part of any organization.");
        }

        // TODO GET TAIL REQUEST FROM request arguments
        TailResponse alert = new TailResponse();
        alert.setId(732L);
        alert.setTitle("shahid-pc | java.lang.IllegalStateException: Subtle failure saving file: __error__testings3-save-file.txt");
        alert.setDescription("Subtle failure saving file: __error__testings3-save-file.txt");
        alert.setDetails("""
        java.lang.IllegalStateException: Subtle failure saving file: __error__testings3-save-file.txt
            at com.shahidfoy.s3_demo.service.impl.S3ServiceSubtleErrors.saveFileToBucket(S3ServiceSubtleErrors.java:49)
            at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103)
            at java.base/java.lang.reflect.Method.invoke(Method.java:580)
            at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:359)
            at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:196)
            at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
            at org.springframework.retry.annotation.AnnotationAwareRetryOperationsInterceptor.invoke(AnnotationAwareRetryOperationsInterceptor.java:165)
            at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)
            at org.springframework.aop.interceptor.AsyncExecutionInterceptor.lambda$invoke$0(AsyncExecutionInterceptor.java:114)
            at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317)
            at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
            at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
            at java.base/java.lang.Thread.run(Thread.java:1583)
        """.stripIndent());

//        List<Note> notes = new ArrayList<>();

        // todo replace owner, repository, and branch by using incoming request parameters
        inariService.handleTailAlert(organization.get().getId(), "shahidfoy", "s3-demo-n1netails", "main", alert); // FIXME: get owner and repo from request

        return ResponseEntity.ok("Pull request initiated");
    }
}
