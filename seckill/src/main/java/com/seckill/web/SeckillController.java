package com.seckill.web;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.dto.SeckillResult;
import com.seckill.entity.Seckill;
import com.seckill.enums.SeckillStatEnum;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.service.SeckillService;

@Controller
//
@RequestMapping(value="seckill")
// 模块 url:模块/资源/{id}/细分
public class SeckillController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	/**
	 * 列表页
	 * 
	 * @param model
	 * @return
	 */
	// 建议 使用list.jsp +Model =ModelAndView
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public String list(Model model) {
		// 获取列表页
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list", list);
		return "list";
	}

	/**
	 * 详情页
	 * 
	 * @param seckillId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "{seckillId}/detail", method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") String seckillId, Model model) {
		if (seckillId == null) {
			return "redirect:/seckill/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		
		if (seckill == null) {
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill", seckill);
		return "detail";
	}

	// ajax json
	@RequestMapping(value = "{seckillId}/exposer", method = RequestMethod.POST)
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId") String seckillId) {
		SeckillResult<Exposer> result = null;
		try {
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true, exposer);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result = new SeckillResult<Exposer>(false, e.getMessage());
		}
		return result;
	}

	// 执行秒杀
	@RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST)
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(
			@PathVariable("seckillId") String seckillId,
			@PathVariable("md5") String md5,
			@CookieValue(value = "killPhone", required = false) Long killPhone) {
		 if (killPhone == null) {
	            return new SeckillResult<SeckillExecution>(false, SeckillStatEnum.NOT_LOGIN.getStateInfo());
	        }

	        try {
	            //SeckillExecution execution = seckillService.executeSeckill(seckillId, killPhone, md5);
	            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, killPhone, md5);
	            return new SeckillResult<SeckillExecution>(true, execution);
	        } catch (RepeatKillException e) {
	            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
	            return new SeckillResult<SeckillExecution>(true, execution);

	        } catch (SeckillCloseException e2) {
	            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
	            return new SeckillResult<SeckillExecution>(true, execution);

	        } catch (SeckillException e) {
	            logger.error(e.getMessage());
	            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
	            return new SeckillResult<SeckillExecution>(true, execution);
	        }
	}
	
	/**
	 * 获取系统当前时间
	 * @return
	 */
	@RequestMapping(value="/time/now",method=RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time(){
		Date now =new Date();
		return new SeckillResult<Long>(true,now.getTime());
	}

}
