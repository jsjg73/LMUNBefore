package com.mycompany.myapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import com.mycompany.myapp.json.XmlParsing;

public class PublicDataService {
	private final String APPKEY ="ysUT4N0M2IspncGRIApzEMkgknQKgXN6UksZY3xYK0VasLokEtQDABvNPEHWOePddgtXtp4rwYI0pIWiR6H37A%3D%3D";
	
	public String getPath(String startX,String startY,String endX,String endY) {
		HttpURLConnection conn = null;
		StringBuilder sb = null;
		try {
			sb = new StringBuilder();
			//request할 url 만들기
			String basic = "http://ws.bus.go.kr/api/rest/pathinfo/getPathInfoByBusNSub?ServiceKey=";
			String opts="&startX="+startX+"&startY="+startY+"&endX="+endX+"&endY="+endY;
			String final_request_url = basic+APPKEY+opts;
			
			// 주소 확인용 디버깅 코드
			//System.out.println(final_request_url);
			
			//url 객체 생성
			URL url = new URL(final_request_url);
			
			conn = (HttpURLConnection) url.openConnection();
			// Request 형식 설정
			conn.setRequestMethod("GET");
			// 키 입력

			// 보내고 결과값 받기
			// 통신 상태 확인 코드.
			int responseCode = conn.getResponseCode();
			//System.out.println(responseCode);
			if (responseCode == 400) {
				System.out.println(
						"400:: 해당 명령을 실행할 수 없음 (실행할 수 없는 상태일 때, 엘리베이터 수와 Command 수가 일치하지 않을 때, 엘리베이터 정원을 초과하여 태울 때)");
			} else if (responseCode == 401) {
				System.out.println("401:: X-Auth-Token Header가 잘못됨");
			} else if (responseCode == 500) {
				System.out.println("500:: 서버 에러, 문의 필요");
			} else { // 성공 후 응답 XML 데이터 문자열로 받기
				sb = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 문자열 parsing 처리
		String str = new XmlParsing().getRouteInfo(sb.toString());
		
		return str;
	}
}
