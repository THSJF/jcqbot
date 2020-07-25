package com.meng.tools.override;

import com.meng.tools.*;
import com.sobte.cqp.jcq.entity.*;
import com.sobte.cqp.jcq.message.*;
import com.sobte.cqp.jcq.util.*;
import java.io.*;

public class CQCodeCC extends CQCode {

    @Override
    public CQImage getCQImage(String code) {
        try {
            // 获取相对路径
            String path = StringHelper.stringConcat("data", File.separator, "image", File.separator, new CoolQCode(code).get("image", "file"), ".cqimg");
            return new CQImage(new IniFile(new File(path)));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String image(File path) {
        CQImage image;
        try {
            image = new CQImage(path, false);
            path = image.download("data/image/", image.getMd5());
            path = FileTypeUtil.checkFormat(path);
            path.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StringHelper.stringConcat("[CQ:image,file=", path.getName(), "]");

    }
}
