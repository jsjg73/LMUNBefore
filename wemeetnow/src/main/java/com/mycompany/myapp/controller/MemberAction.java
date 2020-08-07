package com.mycompany.myapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.myapp.model.FriendBean;
import com.mycompany.myapp.model.MemberBean;
import com.mycompany.myapp.service.FriendServiceImpl;
import com.mycompany.myapp.service.Memberservice;

@Controller
public class MemberAction {

	@Autowired
	private Memberservice memberService;
	@Autowired
	private FriendServiceImpl friendService;

	//email duplicate check ajax
	@RequestMapping(value = "/member_emailcheck.do", method = RequestMethod.POST)
	public String member_emailcheck(@RequestParam("email") String email, Model model) throws Exception {

		int result = memberService.checkMemberEmail(email);
		model.addAttribute("result", result);

		return "member/emailcheckResult";
	}
	
	//sign in form
	@RequestMapping(value = "/member_login.do")
	public String member_login() {
		return "member/loginform";
	}

	//sign up form
	@RequestMapping(value = "/member_join.do")
	public String member_join() {
		return "member/joinform";
	}

	//member information save
	@RequestMapping(value = "/member_join_ok.do", method = RequestMethod.POST)
	public String member_join_ok(@ModelAttribute MemberBean member) throws Exception {
		System.out.println(member.toString());
		memberService.insertMember(member);

		return "redirect:member_login.do";
	}

	
	//sign in verification
	@RequestMapping(value = "/member_login_ok.do", method = RequestMethod.POST)
	public String member_login_ok(@RequestParam("email") String email,
			                      @RequestParam("pwd") String pwd,
			                      HttpSession session, Model model) throws Exception {
		int result=0;		
		MemberBean m = memberService.userCheck(email);		
		if (m == null) {	//member not exists
			
			result = 1;
			model.addAttribute("result", result);
			
			return "member/loginResult";
			
		} else {			//member exists
			if (m.getPwd().equals(pwd)) {	//correct password
				session.setAttribute("email", email);

	            String nickname = m.getNickname();
	            /*
	             * db에서 친구 리스트 가져오기
	             * javascript에서 처리하기 편하게
	             * 각 친구의 메일, 닉, x,y 좌표 각각 스트링으로 넘기기
	             * */
	            List<FriendBean> list = friendService.list(email);
	            StringBuffer fr_email = new StringBuffer();
	            StringBuffer fr_nick = new StringBuffer();
	            StringBuffer fr_x = new StringBuffer();
	            StringBuffer fr_y = new StringBuffer();
	            for(FriendBean fb : list) {
	            	MemberBean f = memberService.userCheck(fb.getEmail2());
	               fr_email.append(f.getEmail()).append("#");
		           fr_nick.append(f.getNickname()).append("#");
		           fr_x.append(f.getX_()).append("#");
		           fr_y.append(f.getY_()).append("#");
	            }
	            session.setAttribute("nickname", nickname);
	            session.setAttribute("fr_email",fr_email);
	            session.setAttribute("fr_nick",fr_nick);
	            session.setAttribute("fr_y",fr_y);
	            session.setAttribute("fr_x",fr_x);
	            
	            //친구 정보 넘기기 끝
	            //============================================
	            
	            return "map/home";
	            
	            // 프론트엔드 새로 합칠때 사용.
	            //return "map/home1.jsp"
				
			} else {		//incorrect password
				result = 2;
				model.addAttribute("result", result);
				
				return "member/loginResult";				
			}
		}
	}

	//member information update form
	@RequestMapping(value = "/member_edit.do")
	public String member_edit(HttpSession session, Model model) throws Exception {

		String email = (String) session.getAttribute("email");

		MemberBean m = memberService.userCheck(email);
				
		model.addAttribute("user", m);
		
		return "member/editform";
	}
	
	//member information update
	@RequestMapping(value = "/member_edit_ok.do", method = RequestMethod.POST)
	public String member_edit_ok( MemberBean member, HttpSession session, Model model) throws Exception {
		String email = member.getEmail();
				
		MemberBean editm = this.memberService.userCheck(email);
			
		if (!member.getPwd().equals(editm.getPwd())) {

			return "member/editResult";
			
		}else {	//correct password
			memberService.updateMember(member);
			session.invalidate();

			return "redirect:member_login.do";
		}
	}
		
	//sign out
	@RequestMapping("/member_logout.do")
	public String logout(HttpSession session) {
		session.invalidate();

		return "member/member_logout";
	}
	
	//member deletion form
	@RequestMapping(value = "/member_del.do")
	public String member_del(HttpSession session, Model model) throws Exception {
		
		String email = (String) session.getAttribute("email");
		MemberBean user = memberService.userCheck(email);
		model.addAttribute("user", user);

		return "member/delform";
	}

	//member deletion completed
	@RequestMapping(value = "/member_del_ok.do", method = RequestMethod.POST)
	public String member_del_ok(MemberBean member, 
							    HttpSession session) throws Exception {
		//넘어온 이메일
		String email = member.getEmail();
		MemberBean user = this.memberService.userCheck(email);

		if (!member.getPwd().equals(user.getPwd())) {

			return "member/deleteResult";
			
		} else {	//correct password
			
			memberService.deleteMember(email);

			session.invalidate();

			return "redirect:member_login.do";
		}
	}

		//	member information
		@RequestMapping(value = "/member_info.do")
		public String member_info(HttpSession session, MemberBean member, Model model) throws Exception {

			String email = (String) session.getAttribute("email");
			
			if(email==null) { // 로그인 세션이 없을때
				model.addAttribute("result",3);
				return "member/loginResult"; //세션 없음 띄워주고 로그인 화면으로 이동
			}else {
				
				MemberBean m = memberService.userCheck(email);
				
				model.addAttribute("user", m);
			}
			
			
			return "member/userinfo";
		}

}
