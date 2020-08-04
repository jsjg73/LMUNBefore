package com.mycompany.myapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycompany.myapp.dao.MapDAO;
import com.mycompany.myapp.json.JsonParsing;
import com.mycompany.myapp.model.Coordinate;
import com.mycompany.myapp.model.Place;
import com.mycompany.myapp.model.stationXY;

@Service
public class MapServiceImpl implements MapService {

	@Autowired
	private MapDAO md;
	@Autowired
	private JsonParsing par;

	private final String URL_HOME = "https://dapi.kakao.com";
	private final String URL_CATEGORY = "/v2/local/search/category.json";
	private final String URL_KEYWORD = "/v2/local/search/keyword.json";
	private final String URL_ADRESS = "/v2/local/search/address.json";

	@Override
	public List<Place> categorySearch(String categoryCode) {
		String url = URL_HOME + URL_CATEGORY + "?";
		String options = "category_group_code/" + categoryCode;
		return getStationCoord(url, options);
	}

	@Override
	public List<Place> categorySearch(String categoryCode, String option) {
		String url = URL_HOME + URL_CATEGORY + "?";
		String options = "category_group_code/" + categoryCode + "/" + option;
		return getStationCoord(url, options);
	}

	@Override
	public List<Place> keywordSearch(String query) {
		String url = URL_HOME + URL_KEYWORD + "?";
		String options = null;
		try {
			options = "query/" + URLEncoder.encode(query, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getStationCoord(url, options);
	}

	@Override
	public List<Place> keywordSearch(String query, String option) {
		String url = URL_HOME + URL_KEYWORD + "?";
		String options = null;
		try {
			options = "query/" + URLEncoder.encode(query, "utf-8") + "/" + option;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getStationCoord(url, options);
	}

	@Override
	public List<Place> addressSearch(String address) {
		String url = URL_HOME + URL_ADRESS + "?";
		String options = null;
		try {
			options = "query/" + URLEncoder.encode(address, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getStationCoord(url, options);
	}

	@Override
	public List<Place> addressSearch(String address, String option) {
		String url = URL_HOME + URL_ADRESS + "?";
		String options = null;
		try {
			options = "query/" + URLEncoder.encode(address, "utf-8") + "/" + option;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getStationCoord(url, options);
	}

	// REST API�� ��û�ؼ� json ���� ������ �޾ƿ� �κ�
	@Override
	public List<Place> getStationCoord(String url_, String options) {
		HttpURLConnection conn = null;
		StringBuilder sb = null;
		try {
			sb = new StringBuilder();
			sb.append(url_);
			StringTokenizer st = new StringTokenizer(options, "/");
			String Authorization = "KakaoAK " + "cdca325d6efe88cfb6c48440908a80c2";
			while (st.hasMoreTokens()) {
				sb.append(st.nextToken()).append("=").append(st.nextToken()).append("&");
			}

			// �ּ� Ȯ�ο� ����� �ڵ�
			String final_request_url = sb.toString();
			System.out.println(final_request_url);
			URL url = new URL(final_request_url);

			conn = (HttpURLConnection) url.openConnection();
			// Request ���� ����
			conn.setRequestMethod("GET");
			// Ű �Է�
			conn.setRequestProperty("Authorization", Authorization);

			// ������ ����� �ޱ�
			// ��� ���� Ȯ�� �ڵ�.
			int responseCode = conn.getResponseCode();
			System.out.println(responseCode);
			if (responseCode == 400) {
				System.out.println(
						"400:: �ش� ����� ������ �� ���� (������ �� ���� ������ ��, ���������� ���� Command ���� ��ġ���� ���� ��, ���������� ������ �ʰ��Ͽ� �¿� ��)");
			} else if (responseCode == 401) {
				System.out.println("401:: X-Auth-Token Header�� �߸���");
			} else if (responseCode == 500) {
				System.out.println("500:: ���� ����, ���� �ʿ�");
			} else { // ���� �� ���� JSON �����͹ޱ�
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

		return par.getPlaceInfo(sb.toString());
	}

	// �߽� ��ǥ ���ϱ�.
	// ����� ��ǥ ���� ���.
	@Override
	public Coordinate getCenter(String[] x, String[] y) {
		Coordinate coor = new Coordinate();
		float n = (float) x.length;
		float sumX = 0;
		float sumY = 0;
		for (int i = 0; i < x.length; i++) {
			sumX += Float.parseFloat(x[i]);
			sumY += Float.parseFloat(y[i]);
		}
		coor.setX(Float.toString(sumX / n));
		coor.setY(Float.toString(sumY / n));
		return coor;
	}

	@Override
	public stationXY getRcm_station(String subName) throws Exception {
		return md.getRcm_station(subName);
	}
}
