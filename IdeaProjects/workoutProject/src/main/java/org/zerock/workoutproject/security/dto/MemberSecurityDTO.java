package org.zerock.workoutproject.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;


import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
public class MemberSecurityDTO extends User  {
  private String mid;
  private String mpw;
  private String email;
  private boolean del;
  private int age;
  private double height;
  private double weight;
  private String phone;

  private Map<String,Object> props;

  public MemberSecurityDTO(String username, String password, String email, int age, double height, double weight, String phone,
                           boolean del,
                           Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
    this.mid = username;
    this.mpw = password;
    this.email = email;
    this.del = del;
    this.age = age;
    this.height = height;
    this.weight = weight;
    this.phone = phone;
  }


}
