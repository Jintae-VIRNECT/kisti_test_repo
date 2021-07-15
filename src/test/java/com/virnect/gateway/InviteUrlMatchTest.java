package com.virnect.gateway;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class InviteUrlMatchTest {
	@Test
	public void workspaceInviteUrlTest() {
		String regexp = "^/workspaces/invite/[a-zA-Z0-9]+/(accept|reject).*$";

		String acceptUrl = "/workspaces/invite/y47O8ZzHd35sTFSq6NRE/accept?lang=ko";
		String rejectUrl = "/workspaces/invite/y47O8ZzHd35sTFSq6NRE/reject?lang=ko";
		String acceptUrlWithEmail = "/workspaces/invite/y47O8ZzHd35sTFSq6NRE/accept?lang=ko&email=sky456139@virnect.com";


		assertTrue(acceptUrl.matches(regexp));
		assertTrue(rejectUrl.matches(regexp));
		assertTrue(acceptUrlWithEmail.matches(regexp));
	}
}
