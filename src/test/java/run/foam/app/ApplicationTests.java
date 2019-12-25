package run.foam.app;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.core.AVOSCloud;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import run.foam.app.model.dto.CommentDataDTO;
import run.foam.app.model.entity.CommentInfo;
import run.foam.app.model.vo.CommentVo;

import java.util.ArrayList;
import java.util.List;

class ApplicationTests {

    @Test
    public void findCommentByfactor1(){
        AVOSCloud.initialize("f31J5kVi0JdGq11UOxhQJ12Y-gzGzoHsz", "urv1VlxqDU3Naidx9UGV9zmW");
        final CommentDataDTO commentDataDTO = new CommentDataDTO();

        AVQuery<AVObject> query = new AVQuery<>("Comment");
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(List<AVObject> objs) {

                List<CommentVo> list = new ArrayList<>();
                // students 是包含满足条件的 Student 对象的数组
                for (AVObject avObject:objs){
                    CommentVo commentVo = new CommentVo();
                    commentVo.setComment(avObject.getString("comment"));
                    commentVo.setInsertedAt(avObject.getDate("insertedAt"));
                    commentVo.setIp(avObject.getString("ip"));
                    commentVo.setLink(avObject.getString("link"));
                    commentVo.setMail(avObject.getString("mail"));
                    commentVo.setNick(avObject.getString("nick"));
                    commentVo.setPid(avObject.getString("pid"));
                    commentVo.setRid(avObject.getString("rid"));
                    commentVo.setUa(avObject.getString("ua"));
                    commentVo.setUrl(avObject.getString("url"));
                    list.add(commentVo);
                }
                commentDataDTO.setResultList(list);
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
    }

}
