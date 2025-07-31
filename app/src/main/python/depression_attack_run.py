import torch
import data_processing
import depression_attack_model
from os.path import dirname, join


def run(data_path, new_model_path): # data_path为要测试的数据文件路径；new_model_path为微调后的模型文件路径
    data_path = join(dirname(__file__), data_path)
    new_model_path = join(dirname(__file__), new_model_path)

    # 1.获取数据
    ppg_data, ir_data, ired_data, gsr_data, temp_data = data_processing.deal_data(data_path, 1)

    # 2.调用模型处理
    net = depression_attack_model.Classifier()
    net.load_state_dict(torch.load(new_model_path, map_location='cpu'))
    output = net(torch.tensor(ppg_data, dtype=torch.float32), torch.tensor(ir_data, dtype=torch.float32), torch.tensor(ired_data, dtype=torch.float32),
                 torch.tensor(gsr_data, dtype=torch.float32), torch.tensor(temp_data, dtype=torch.float32))

    # 3.后续数据处理
    result = output.argmax(1)
    result = result.numpy()
    return str(result[0]) # 输出类型为numpy数组，例：[1,1,1,1,1,1,1,1,1]，其中数组0为平静、1为发作

# if __name__ == '__main__':
#     result = run('./2022-10-21 16-22-26/', './')
#     print(result)