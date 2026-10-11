#version 330

// Copy of dynamicimports.glsl, as in vanilla's core shaders.
layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

// Flat-colour silhouette of the sampled (premultiplied alpha) item image.
void main() {
    float a = texture(Sampler0, texCoord0).a * vertexColor.a;
    if (a == 0.0) {
        discard;
    }
    fragColor = vec4(vertexColor.rgb * a, a) * ColorModulator;
}
