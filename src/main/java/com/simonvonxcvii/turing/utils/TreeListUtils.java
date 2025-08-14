package com.simonvonxcvii.turing.utils;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO: 2023/8/28 删除该工具类
@Deprecated(forRemoval = true)
public class TreeListUtils {

    /**
     * 上级记录id分隔符
     */
    public static final String SEP = ",";

    /**
     * 树形化 list
     */
    public static <T> void tree(List<T> list, Function<T, String> id, Function<T, String> pid, BiConsumer<T, T> consumer) {
        Map<String, T> detailVoMap = list
                .stream().collect(Collectors.toMap(id, Function.identity()));

        Function<T, String> pidWrapper = t -> getParentId(pid.apply(t));
        list.forEach(node -> {
            String parentId = pidWrapper.apply(node);
            if (StringUtils.hasText(parentId)) {
                T parent = detailVoMap.get(parentId);
                consumer.accept(parent, node);
            } else {
                consumer.accept(null, node);
            }
        });
    }

    /**
     * 获取父节点 id
     */
    public static String getParentId(String pid) {
        if (!StringUtils.hasText(pid)) {
            return pid;
        }
        if (!pid.contains(SEP)) {
            return pid;
        }

        String[] ids = StringUtils.commaDelimitedListToStringArray(pid);
        return ids[ids.length - 1];
    }

}
