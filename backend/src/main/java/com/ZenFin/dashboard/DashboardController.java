package com.ZenFin.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/dashboard")
public class DashboardController {


//  @GetMapping("overview")
//  public ResponseEntity<OverviewResponse> overview(){
//
//    return ResponseEntity.ok(dashboardService.overview());
//  }


}


