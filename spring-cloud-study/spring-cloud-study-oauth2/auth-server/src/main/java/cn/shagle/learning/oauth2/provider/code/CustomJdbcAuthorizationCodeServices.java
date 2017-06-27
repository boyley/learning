package cn.shagle.learning.oauth2.provider.code;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;

import javax.sql.DataSource;
import java.sql.Types;

/**
 * Created by lenovo on 2017/6/27.
 */
public class CustomJdbcAuthorizationCodeServices extends JdbcAuthorizationCodeServices {

    private static final String DEFAULT_INSERT_STATEMENT = "insert into oauth_code (username, code, authentication) values (?, ?, ?)";
    private String insertAuthenticationSql = DEFAULT_INSERT_STATEMENT;
    private final JdbcTemplate jdbcTemplate;


    public CustomJdbcAuthorizationCodeServices(DataSource dataSource) {
        super(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        String username = authentication.getUserAuthentication().getName();
        jdbcTemplate.update(insertAuthenticationSql,
                new Object[] {username, code, new SqlLobValue(SerializationUtils.serialize(authentication)) }, new int[] {
                        Types.VARCHAR, Types.VARCHAR, Types.BLOB });
    }
}
