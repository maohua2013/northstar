package tech.xuanwu.northstar.domain.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.common.utils.FieldUtils;
import tech.xuanwu.northstar.strategy.api.constant.ModuleState;
import xyz.redtorch.pb.CoreEnum.PositionDirectionEnum;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 模组状态
 * @author KevinHuangwl
 *
 */
@Slf4j
@Data
public class ModuleStatusHolder {

	@Getter
	private String moduleName;
	
	@Getter
	private ModuleStateMachine stateMachine;
	
	protected ConcurrentMap<String, ModulePosition> longPositions;
	
	protected ConcurrentMap<String, ModulePosition> shortPositions;
	
	private String holdingTradingDay;
	
	private int countOfOpeningToday;
	
	private double accountAvailable;
	
	public ModuleStatusHolder(String name) {
		this.moduleName = name;
		this.stateMachine = new ModuleStateMachine(name, ModuleState.EMPTY);
		this.longPositions = new ConcurrentHashMap<>();
		this.shortPositions = new ConcurrentHashMap<>();
	}

	public void manuallyUpdatePosition(ModulePosition position) {
		Map<String, ModulePosition> positionMap = getPositionMap(position.getDirection());
		positionMap.put(position.contract().getUnifiedSymbol(), position);
		ModuleState state = FieldUtils.isLong(position.getDirection()) ? ModuleState.HOLDING_LONG : ModuleState.HOLDING_SHORT;
		log.info("[{}] 手动变更模组状态：[{}]", getModuleName(), state);
		stateMachine.setCurState(state);
		stateMachine.setOriginState(state);
	}
	
	public void manuallyRemovePosition(String unifiedSymbol, PositionDirectionEnum dir) {
		Map<String, ModulePosition> positionMap = getPositionMap(dir);
		positionMap.remove(unifiedSymbol);
		log.info("[{}] 手动变更模组状态：[{}]", getModuleName(), ModuleState.EMPTY);
		stateMachine.setCurState(ModuleState.EMPTY);
		stateMachine.setOriginState(ModuleState.EMPTY);
	}
	
	public boolean at(ModuleState state) {
		return stateMachine.getState() == state;
	}
	
	public double getHoldingProfit() {
		double p1 = longPositions.values().stream().mapToDouble(ModulePosition::getProfit).reduce(0D, (a,b) -> a+b);
		double p2 = shortPositions.values().stream().mapToDouble(ModulePosition::getProfit).reduce(0D, (a,b) -> a+b);
		return p1 + p2;
	}
	
	public boolean isSameDayHolding(String currentTradingDay) {
		return StringUtils.equals(currentTradingDay, holdingTradingDay);
	}
	
	private void opening(TradeField trade, OrderField order) {
		log.info("[{}] 模组开仓{}", getModuleName(), trade.getContract().getSymbol());
	}
	
	private void closing(TradeField trade) {
		log.info("[{}] 模组平仓{}", getModuleName(), trade.getContract().getSymbol());
	}
	
	private Map<String, ModulePosition> getPositionMap(PositionDirectionEnum dir){
		if(dir == PositionDirectionEnum.PD_Long) {
			return longPositions;
		}
		if(dir == PositionDirectionEnum.PD_Short) {
			return shortPositions;
		}
		throw new IllegalArgumentException("非法持仓方向：" + dir);
	}
}
