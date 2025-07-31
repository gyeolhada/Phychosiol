import numpy as np
import torch
import Datapreprocess
from scipy.signal import savgol_filter
import feature_extractor
import torch.nn.functional as F
from os.path import dirname, join


def temp_preprocess(signal, SIGNAL_FS):
    removemean = Datapreprocess.remove_mean(signal)
    remove_powfren = Datapreprocess.fft_filter(removemean, SIGNAL_FS, [49, 51, 149, 151, 249, 251],
                                               type='bandstop')
    z_data = Datapreprocess.normalize_1c(remove_powfren, mode='maxmin')
    filter_data = savgol_filter(z_data, 99, 1, mode='nearest')
    return filter_data


def ir_preprocess(signal, SIGNAL_FS):
    removemean_ir = Datapreprocess.remove_mean(signal)
    remove_powfren_ir = Datapreprocess.fft_filter(removemean_ir, SIGNAL_FS,
                                                  [49, 51, 149, 151, 249, 251], type='bandstop')
    filter_ir = Datapreprocess.BPF_FIR(remove_powfren_ir, SIGNAL_FS, 0.1, 5)
    z_ir = Datapreprocess.normalize_1c(filter_ir, mode='maxmin')
    return z_ir


def gsr_preprocess(signal, SIGNAL_FS):
    removemean = Datapreprocess.remove_mean(signal)
    remove_powfren = Datapreprocess.fft_filter(removemean, SIGNAL_FS, [49, 51, 149, 151, 249, 251],
                                               type='bandstop')
    z_data = Datapreprocess.normalize_1c(remove_powfren, mode='maxmin')
    filter_data = Datapreprocess.BPF_FIR(z_data, SIGNAL_FS, 0.000001, 0.3)
    return filter_data


def ppg_preprocess(signal, SIGNAL_FS):
    removemean = Datapreprocess.remove_mean(signal)
    remove_powfren = Datapreprocess.fft_filter(removemean, SIGNAL_FS, [49, 51, 149, 151, 249, 251],
                                               type='bandstop')
    f_data = Datapreprocess.fft_filter(remove_powfren, SIGNAL_FS, [0, 0.5], type='bandstop')
    filter_data = Datapreprocess.BPF_FIR(f_data, SIGNAL_FS, 0.1, 12)
    z_data = Datapreprocess.normalize_1c(filter_data, mode='maxmin')
    return z_data


def cut_signal(signal, SIGNAL_FS, second=1):
    features = []
    num = int(len(signal) / SIGNAL_FS)  # 数据分段 每秒为一段数据
    for n in range(second):  #
        # if (n+1)*SIGNAL_FS > num:
        #     break
        # else:
        features.append(signal[n * SIGNAL_FS:(n + 1) * SIGNAL_FS])

    features_numpy = np.array(features)
    # print(features_numpy.shape)
    features_numpy = features_numpy.reshape(features_numpy.shape[0], 1, features_numpy.shape[1])
    print(features_numpy.shape)
    features = torch.tensor(features_numpy, dtype=torch.float32)
    return features


def run(root_path):
    try:
        root_path = join(dirname(__file__), root_path)
        Max30102 = root_path +'Max30102.txt'
        LMT70 = root_path +'LMT70.txt'
        Pluse_sensor = root_path +'Pulse_sensor.txt'
        Grove = root_path +'Grove.txt'
# ir_data = np.loadtxt(Max30102)
# ired_data = np.loadtxt(Max30102)
# temp_data = np.loadtxt(LMT70)
# ppg_data = np.loadtxt(Pluse_sensor)
# gsr_data = np.loadtxt(Grove)
        # 读取本地原始数据 txt,使用字符串拼接
        # Max30102采样率为400, LMT70采样率为100, Pluse_sensor采样率为400, Grove采样率为200
        with open(Max30102) as f:
            lines = f.readlines()
        ir_data = np.loadtxt(lines)
        ired_data = np.loadtxt(lines)
        with open(LMT70) as f:
            lines = f.readlines()
        temp_data = np.loadtxt(lines)
        with open(Pluse_sensor) as f:
            lines = f.readlines()
        ppg_data = np.loadtxt(lines)
        with open(Grove) as f:
            lines = f.readlines()
        gsr_data = np.loadtxt(lines)

        #截取1s数据
        ir_data = ir_data[:400]
        ired_data = ired_data[:400]
        temp_data = temp_data[:100]
        ppg_data = ppg_data[:400]
        gsr_data = gsr_data[:200]

        # 数据提取 转换
        ir_signal = ir_data[:, -1]
        ired_signal = ired_data[:, -2]
        temp_signal = temp_data[:, -1]
        ppg_signal = ppg_data[:, -1]
        gsr_signal = gsr_data[:, -1]

        # 数据预处理
        temp_signal = temp_preprocess(temp_signal, 100)
        ir_signal = ir_preprocess(ir_signal, 400)
        ired_signal = ir_preprocess(ired_signal, 400)
        gsr_signal = gsr_preprocess(gsr_signal, 200)
        ppg_signal = ppg_preprocess(ppg_signal, 400)

        # 数据切分 切1s的数据
        temp_signal = cut_signal(temp_signal, 100, second=1)
        ired_signal = cut_signal(ired_signal, 400, second=1)
        ppg_signal = cut_signal(ppg_signal, 400, second=1)
        ir_signal = cut_signal(ir_signal, 400, second=1)
        gsr_signal = cut_signal(gsr_signal, 200, second=1)
        # 深度学习模型处理

        net = feature_extractor.make_Net()
        model_wearshot = join(dirname(__file__), "data/model_wearshot.pth")
        net = torch.load(model_wearshot, map_location=torch.device('cpu')).cpu()
        output = net(gsr_signal, ppg_signal, ir_signal, ired_signal, temp_signal)
        ####rint(output.shape)

        ####print('模型输出形状:', output.size())
        ####print('模型输出:', output, '数据类型', type(output))

        # 4.生成反馈数据
        # 4.1 各类别的概率值
        prob = F.softmax(output[0], dim=0)  # softmax是一种概率分布的计算方式，由固定公式
        prob_list = [p.item() for p in prob]  # 转为python列表对象，外面数据类型为 list， 列表里面表示概率的数为32位浮点型
        prob_numpy = prob.detach().numpy()  # 转为numpy数组对象，如果列表对象传不到安卓，则尝试用numpy对象传
        ####print('所预测各类别的概率分布为：', prob_list)
        # 4.2 预测类别的编号
        result = prob.argmax()  # 概率分布种最大概率的编号就是最终预测的类别编号
        result = result.item()  # 转为普通python整数对象，数据类型是 int
        ####print('预测类别的编号为', result)
        # 类别对应情绪分别如下
        labels_name = ['0', '1', '2', '3', '4', '5']
        labels_name = ['neutral', 'sad', 'happy', 'anger', 'disgust', 'fear']
        return str(result)
        # return 1
    except Exception as e:
        return e

if __name__ == "__main__":
    run()
