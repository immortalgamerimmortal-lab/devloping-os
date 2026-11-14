# AOSP path: from app prototype to supported platform build

This project is not an AOSP tree. A genuine OS effort requires an Android platform checkout, an explicitly supported product target, maintainers for security updates, device-specific vendor support, and independent validation. A launcher preview cannot be promoted into an OS by changing its branding.

## Target and build baseline

Start with the **AOSP Cuttlefish virtual device** as the first reproducible engineering target; it avoids implying that an unsupported physical phone can boot the result. Pin the AOSP manifest/release branch and matching build instructions at project kickoff. Build on a supported 64-bit Linux workstation with the release's documented JDK, Repo tool, Git, disk/RAM capacity, and AOSP prerequisite packages. This application currently targets Android API 35, but that is not a selected or validated AOSP release branch. Decide the platform branch and Cuttlefish image version together before the first platform checkout.

Do not announce real-device support until a named device has compatible bootloader, kernel, vendor partition/interfaces, signing arrangements, and a maintainer-backed update plan. Device enablement is separate from application UI work.

## Phases

1. **Finish and validate the app prototype.** Sync and compile in Android Studio, exercise it across emulator sizes and API levels, test biometric cancellation/unavailable/enrolled cases, and run accessibility checks. Keep every prototype security claim qualified.
2. **Freeze the platform target and threat model.** Document assets, attacker capabilities, physical-access assumptions, recovery/debug interfaces, data-at-rest needs, network/update trust roots, privacy expectations, supported lifetime, and incident-response ownership. Define a support window and security bulletin response SLA before devices are promised.
3. **Reproduce a stock Cuttlefish build.** Record manifest revision, build host, build commands, image hashes, signing keys' custody boundaries, and a clean baseline boot/test report before modifying platform code.
4. **Integrate the experience as platform components.** Adapt reusable visuals into an Android launcher project and, only where justified, System UI/Quick Settings/keyguard resources and privileged services. Use platform service APIs and existing permission/notification/authentication contracts. Keep privileged permissions to the minimum and review every platform signature/privapp allowlist change.
5. **Harden and validate the product.** Require production-style AVB-signed images, verified boot chain and locked-state behavior, SELinux enforcing with narrowly reviewed policy, standard app sandboxing, file-based encryption, hardware-backed Keystore where supported, platform credential/biometric services, and signed OTA with rollback protection. Validate permission mediation and privacy surfaces against the branch's CTS/VTS and relevant security tests. Run build, boot, compatibility, SELinux-denial, update, rollback, encryption, recovery, and red-team reviews.
6. **Establish operations before release.** Protect release keys offline/HSM-backed under controlled custody, publish reproducible build and image-signing procedures, test staged OTA and recovery, set support/EOL dates, monitor upstream Android bulletins, triage vulnerabilities, and rehearse emergency updates. A virtual-device result does not prove a physical device's hardware-backed properties.

## Required release evidence

For each exact supported image, retain source revision, build provenance, image hashes, AVB chain and rollback-index checks, SELinux enforcing evidence and policy review, CTS/VTS results, encryption and Keystore capability results, keyguard/biometric integration tests, OTA signature/rollback/recovery tests, and an owner-approved vulnerability-response policy. Any unsupported hardware-backed guarantee must be stated as a limitation, not inferred from an emulator.
