# 🎨 Shaders & LabPBR POM Setup

Jane Moose features full **LabPBR 1.3** specification textures with Parallax Occlusion Mapping (POM) height maps and specular roughness/metallic maps.

---

## 🌟 Compatible Shader Packs
* **Complementary Shaders (Reimagined & Unbound)**
* **Iris / Oculus Shaders**
* **BSL Shaders**
* **SEUS PTGI / Renewed**
* **MakeUp Ultra Fast**

---

## ⚙️ Recommended Shader Settings

To experience true 3D surface depth on tire lugs, honeycomb grilles, diamond plate, and sheet metal louvers:

1. In your Shader Pack Settings, navigate to **Surface / Materials**.
2. Set **Material Format / RP Support** to **LabPBR** (or *Auto*).
3. Set **Parallax Occlusion Mapping (POM)** to **ON**.
4. Set **POM Depth** to `0.25` – `0.50` (recommended depth scale).
5. Ensure **Slope Normals** and **Directional Lightmaps** are enabled.

> [!NOTE]
> All decals and painted lettering ("JANE MOOSE 4020") have clamped height values to prevent raymarching stepping artifacts, ensuring text renders vector-crisp under all shader packs.
