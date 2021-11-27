package tech.xuanwu.northstar.main.restful;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.xuanwu.northstar.common.event.InternalEventBus;
import tech.xuanwu.northstar.common.model.PlaybackDescription;
import tech.xuanwu.northstar.common.model.ResultBean;
import tech.xuanwu.northstar.main.manager.ModuleManager;
import tech.xuanwu.northstar.main.playback.PlaybackStatRecord;
import tech.xuanwu.northstar.main.service.PlaybackService;
import tech.xuanwu.northstar.strategy.common.model.entity.ModuleDealRecord;
import tech.xuanwu.northstar.strategy.common.model.entity.ModuleTradeRecord;

@RestController
@RequestMapping("/pb")
public class PlaybackController {
	
	@Autowired
	private PlaybackService playbackService;
	
	@Autowired 
	private ModuleManager moduleMgr;
	
	@Autowired
	private InternalEventBus eventBus;

	/**
	 * 开始回测
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception 
	 */
	@PostMapping("/play")
	public ResultBean<Void> play(@RequestBody PlaybackDescription playbackDescription) throws Exception{
		playbackService.play(playbackDescription, moduleMgr, eventBus);
		return new ResultBean<>(null);
	}
	
	/**
	 * 查询回测进度
	 * @param playId
	 * @return
	 */
	@GetMapping("/play/process")
	public ResultBean<Integer> playProcess(){
		return new ResultBean<>(playbackService.playProcess());
	}
	
	/**
	 * 查询回测账户余额
	 * @param moduleName
	 * @return
	 */
	@GetMapping("/balance")
	public ResultBean<Integer> playbackBalance(@NotNull String moduleName){
		return new ResultBean<>(playbackService.playbackBalance(moduleName));
	}
	
	/**
	 * 查询回测成交记录
	 * @return
	 */
	@GetMapping("/records/trade")
	public ResultBean<List<ModuleTradeRecord>> playbackTradeRecords(@NotNull String moduleName){
		return new ResultBean<>(playbackService.getTradeRecords(moduleName));
	}
	
	/**
	 * 查询回测交易记录
	 * @param moduleName
	 * @return
	 */
	@GetMapping("/records/deal")
	public ResultBean<List<ModuleDealRecord>> playbackDealRecords(@NotNull String moduleName){
		return new ResultBean<>(playbackService.getDealRecords(moduleName));
	}
	
	/**
	 * 查询回测统计结果
	 * @param moduleName
	 * @return
	 */
	@GetMapping("/records/stat")
	public ResultBean<PlaybackStatRecord> playbackStatRecord(@NotNull String moduleName){
		return new ResultBean<>(playbackService.getPlaybackStatRecord(moduleName));
	}
	
	
	/**
	 * 查询回测就绪状态
	 * @return
	 */
	@GetMapping("/readiness")
	public ResultBean<Boolean> getPlaybackReadiness(){
		return new ResultBean<>(playbackService.getPlaybackReadiness());
	}
}
