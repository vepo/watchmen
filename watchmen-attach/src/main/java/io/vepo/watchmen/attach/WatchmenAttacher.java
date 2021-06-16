package io.vepo.watchmen.attach;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.bytebuddy.agent.ByteBuddyAgent;

public class WatchmenAttacher {
	private static final ByteBuddyAgent.AttachmentProvider ATTACHMENT_PROVIDER = new ByteBuddyAgent.AttachmentProvider.Compound(
			ByteBuddyAgent.AttachmentProvider.ForEmulatedAttachment.INSTANCE,
			ByteBuddyAgent.AttachmentProvider.ForModularizedVm.INSTANCE,
			ByteBuddyAgent.AttachmentProvider.ForJ9Vm.INSTANCE,
			new CachedAttachmentProvider(ByteBuddyAgent.AttachmentProvider.ForStandardToolsJarVm.JVM_ROOT),
			new CachedAttachmentProvider(ByteBuddyAgent.AttachmentProvider.ForStandardToolsJarVm.JDK_ROOT),
			new CachedAttachmentProvider(ByteBuddyAgent.AttachmentProvider.ForStandardToolsJarVm.MACINTOSH),
			new CachedAttachmentProvider(ByteBuddyAgent.AttachmentProvider.ForUserDefinedToolsJar.INSTANCE));

	public static void attach() {
		ByteBuddyAgent.attach(AgentJarFileHolder.INSTANCE.agentJarFile,
				ByteBuddyAgent.ProcessProvider.ForCurrentVm.INSTANCE, "", ATTACHMENT_PROVIDER);
	}

	private enum AgentJarFileHolder {
		INSTANCE;

		// initializes lazily and ensures it's only loaded once
		final File agentJarFile = getAgentJarFile();

		private static File getAgentJarFile() {
			try (InputStream agentJar = WatchmenAttacher.class.getResourceAsStream("/watchmen-agent.jar")) {
				if (agentJar == null) {
					throw new IllegalStateException("Agent jar not found");
				}
				String hash = md5Hash(WatchmenAttacher.class.getResourceAsStream("/watchmen-agent.jar"));
				File tempAgentJar = new File(System.getProperty("java.io.tmpdir"),
						"elastic-apm-agent-" + hash + ".jar");
				if (!tempAgentJar.exists()) {
					try (OutputStream out = new FileOutputStream(tempAgentJar)) {
						byte[] buffer = new byte[1024];
						for (int length; (length = agentJar.read(buffer)) != -1;) {
							out.write(buffer, 0, length);
						}
					}
				} else if (!md5Hash(new FileInputStream(tempAgentJar)).equals(hash)) {
					throw new IllegalStateException(
							"Invalid MD5 checksum of " + tempAgentJar + ". Please delete this file.");
				}
				return tempAgentJar;
			} catch (NoSuchAlgorithmException | IOException e) {
				throw new IllegalStateException(e);
			}
		}

	}

	static String md5Hash(InputStream resourceAsStream) throws IOException, NoSuchAlgorithmException {
		try (InputStream agentJar = resourceAsStream) {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			DigestInputStream dis = new DigestInputStream(agentJar, md);
			while (dis.read(buffer) != -1) {
			}
			return String.format("%032x", new BigInteger(1, md.digest()));
		}
	}
}
