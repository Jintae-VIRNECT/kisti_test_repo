package com.virnect.content.global.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jeonghyeon.chang (johnmark)
 * @email practice1356@gmail.com
 * @since 2020.02.29
 */

@Slf4j
@Order(1)
public class ReadableRequestWrapperFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		ReadableRequestWrapper wrapper = new ReadableRequestWrapper((HttpServletRequest)request);
		chain.doFilter(wrapper, response);
	}

	@Override
	public void destroy() {

	}
}
