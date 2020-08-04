package com.mycompany.myapp.dao;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mycompany.myapp.model.stationXY;

@Repository
public class MapDAOImpl implements MapDAO{
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public stationXY getRcm_station(String subname) throws Exception {
		// 값 전달 디버깅용 코드
//		System.out.println("dao 호출 완료 : "+subname);
//		stationXY xy = sqlSession.selectOne("mapns.selectStation", subname);
//		System.out.println("dao_xy:"+xy);
//		System.out.println("xy_list:"+xy_list);
//		return xy;
		return sqlSession.selectOne("mapns.selectStation", subname);
	}
	
}
