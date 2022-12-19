COMPILE_DEPS = CORE_DEPS + JACKSON + KRYO + CLI + [
    "//core/store/serializers:onos-core-serializers",
    "//core/store/primitives:onos-core-primitives",
    "//core/common:onos-core-common-native",
]

osgi_jar_with_tests(
    karaf_command_packages = ["org.onosproject.intender-agent"],
    deps = COMPILE_DEPS,
)

onos_app(
    category = "Utility",
    description = "Check availability",
    title = "Intender Agent",
    url = "https://github.com/purseclab/ibn-fuzzer",
)
