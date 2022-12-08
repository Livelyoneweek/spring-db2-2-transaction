package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LogRepository {
    private final EntityManager em;

    //물리 트랜잭션 자체가 분리되어짐
    @Transactional(propagation = Propagation.REQUIRES_NEW) //별도의 데이터베이스 커넥션을 사용함, 익셉션이 터져도 서비스 단에서의 트랜잭셔널 커넥션에 rollbackOnly가 새겨지지않음
    public void save(Log logMessage) {
        log.info("log 저장");
        em.persist(logMessage);
        if (logMessage.getMessage().contains("로그예외")) {
            log.info("log 저장시 예외 발생");
            throw new RuntimeException("예외 발생");
        }
    }
    public Optional<Log> find(String message) {
        return em.createQuery("select l from Log l where l.message = :message", Log.class)
                .setParameter("message", message)
                .getResultList().stream().findAny();
    }
}

