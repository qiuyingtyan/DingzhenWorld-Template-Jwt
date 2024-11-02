package com.example.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class FlowUtils {

    // 注入StringRedisTemplate
    @Resource
    StringRedisTemplate template;

    // 限制一次检查
    public boolean limitOnceCheck(String key, int blockTime){
        return this.internalCheck(key, 1, blockTime, (overclock) -> false);
    }

    // 限制一次升级检查
    public boolean limitOnceUpgradeCheck(String key, int frequency, int baseTime, int upgradeTime){
        return this.internalCheck(key, frequency, baseTime, (overclock) -> {
                    if (overclock)
                        template.opsForValue().set(key, "1", upgradeTime, TimeUnit.SECONDS);
                    return false;
                });
    }

    // 限制周期检查
    public boolean limitPeriodCheck(String counterKey, String blockKey, int blockTime, int frequency, int period){
        return this.internalCheck(counterKey, frequency, period, (overclock) -> {
                    if (overclock)
                        template.opsForValue().set(blockKey, "", blockTime, TimeUnit.SECONDS);
                    return !overclock;
                });
    }

    // 内部检查方法
    private boolean internalCheck(String key, int frequency, int period, LimitAction action){
        // 获取计数器的值
        String count = template.opsForValue().get(key);
        if (count != null) {
            // 增加计数器的值
            long value = Optional.ofNullable(template.opsForValue().increment(key)).orElse(0L);
            int c = Integer.parseInt(count);
            // 如果计数器的值不等于增加后的值，则重置计数器的过期时间
            if(value != c + 1)
                template.expire(key, period, TimeUnit.SECONDS);
            // 运行动作
            return action.run(value > frequency);
        } else {
            // 如果计数器不存在，则设置计数器的值为1，并设置过期时间
            template.opsForValue().set(key, "1", period, TimeUnit.SECONDS);
            return true;
        }
    }

    // 限制动作接口
    private interface LimitAction {
        boolean run(boolean overclock);
    }
}
